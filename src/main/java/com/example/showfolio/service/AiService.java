package com.example.showfolio.service;

import com.example.showfolio.dto.*;
import com.example.showfolio.entity.AiUsage;
import com.example.showfolio.exception.DailyLimitExceededException;
import com.example.showfolio.exception.MonthlyLimitExceededException;
import com.example.showfolio.exception.PremiumRequiredException;
import com.example.showfolio.exception.ProjectNotFoundException;
import com.example.showfolio.prompt.AiPromptType;
import com.example.showfolio.repository.AiUsageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient.Builder chatClientBuilder;
    private final AiUsageRepository aiUsageRepository;

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    private final UserTechStackRepository userTechStackRepository;
    private final FeedRepository feedRepository;

    // Jackson 객체 파서 (스레드 세이프)
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 템플릿 엔진
    private final TemplateEngine templateEngine;

    // 유료 회원 일일 토큰 한도 (input + output 합계 기준), 10만자
    private static final int DAILY_TOKEN_LIMIT = 100_000;
    // 유료 회원 월별 토큰 한도, 200만자
    private static final int MONTHLY_TOKEN_LIMIT = 2_000_000;

    // AI 기능 사용 가능 여부 검증 (구독 + 토큰 한도)
    // 검증 통과 시 (todayUsed, monthUsed) 반환 - 응답 조립에 재사용
    private UsageSnapshot validateAndGetUsage(Long memberId) {

        // 1. 구독 검증 (회원 담당자 작업 완료 후 활성화)

        // 회원 담당자가 Member 엔티티에 구독 필드(subscriptionType, subscriptionExpiredAt) + isPremium() 메서드 추가 후 활성화
         Member member = memberRepository.findById(memberId)
             .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

         if (!member.isPremium()) {
             throw new PremiumRequiredException();
         }


        // 2. 당일 토큰 할당량 한도 검증

        // 오늘 0시 0분 계산 -> todayStart: 2026-05-30T00:00:00
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        // 오늘 0시 0분 이후 사용량 합계 조회
        int todayUsed = aiUsageRepository.sumTokensSince(memberId, todayStart);
        if (todayUsed >= DAILY_TOKEN_LIMIT) {
            throw new DailyLimitExceededException(todayUsed, DAILY_TOKEN_LIMIT);
        }

        // 3. 당월 토큰 할당량 한도 검증

        // 이번 달 1일, 0시 0분 계산
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        // 이번 달 1일, 0시 0분 이후 사용량 합계 조회
        int monthUsed = aiUsageRepository.sumTokensSince(memberId, monthStart);
        if (monthUsed >= MONTHLY_TOKEN_LIMIT) {
            throw new MonthlyLimitExceededException(monthUsed, MONTHLY_TOKEN_LIMIT);
        }

        return new UsageSnapshot(todayUsed, monthUsed);
    }

    // 검증 시점의 사용량 스냅샷 (응답 조립에 재사용하기 위함)
    private record UsageSnapshot(int todayUsed, int monthUsed) {
    }

    // AI API 호출 결과 사용량 기록 + 응답 조립
    // - 사용량 DB 기록
    // - 일일/월별 누적 계산
    // TokenUsageInfo (Daily + Monthly를 묶은 정보)
    private TokenUsageInfo recordUsageAndCalculate(
            Long memberId, String featureType,
            UsageSnapshot snapshot,
            int inputTokens, int outputTokens) {

        // 사용량 기록
        aiUsageRepository.save(
                AiUsage.of(memberId, featureType, inputTokens, outputTokens)
        );

        log.info("{} 완료. memberId={}, input={}, output={}",
                featureType, memberId, inputTokens, outputTokens);

        // 누적 계산
        int thisCallTotal = inputTokens + outputTokens;
        int newDailyTotal = snapshot.todayUsed() + thisCallTotal;
        int newMonthlyTotal = snapshot.monthUsed() + thisCallTotal;

        return new TokenUsageInfo(
                new TokenUsage.Daily(
                        newDailyTotal, DAILY_TOKEN_LIMIT,
                        DAILY_TOKEN_LIMIT - newDailyTotal),
                new TokenUsage.Monthly(
                        newMonthlyTotal, MONTHLY_TOKEN_LIMIT,
                        MONTHLY_TOKEN_LIMIT - newMonthlyTotal)
        );
    }

    // 응답에 들어갈 사용량 정보 묶음
    private record TokenUsageInfo(
            TokenUsage.Daily daily,
            TokenUsage.Monthly monthly
    ) {

    }

    //===================================================================================================//

    // 프로젝트 설명 AI 개선
    @Transactional
    public DescriptionImproveResponse improveDescription(
            Long memberId, DescriptionImproveRequest request) {

        // 1. AI 기능 사용 가능 여부 검증 (구독 + 토큰 한도)
        UsageSnapshot snapshot = validateAndGetUsage(memberId);

        // 2. LLM 호출 : ChatClient로 Gemini 호출 (메타데이터까지 받기)
        ChatResponse response = chatClientBuilder.build()
                .prompt()
                .system(AiPromptType.DESCRIPTION_IMPROVE.system())
                .user(request.description())
                .call()
                .chatResponse();

        // 3. 결과 + 토큰 사용량 추출
        String improvedDescription = response.getResult().getOutput().getText();
        Usage usage = response.getMetadata().getUsage();
        int inputTokens = usage.getPromptTokens();
        int outputTokens = usage.getCompletionTokens();

        // 4. AI API 호출 결과 사용량 기록 + 응답 조립
        TokenUsageInfo usageInfo = recordUsageAndCalculate(
                memberId, "DESCRIPTION_IMPROVE", snapshot, inputTokens, outputTokens);

        return new DescriptionImproveResponse(
                improvedDescription, usageInfo.daily(), usageInfo.monthly());
    }

    //===================================================================================================//

// 이력서 변환에서 필요한 정보인 프로젝트와 기술 스택을 한 번에 가져옴.
//    @Query("SELECT p FROM Project p " +
//            "LEFT JOIN FETCH p.techStacks " +
//            "WHERE p.id = :id")
//    Optional<Project> findByIdWithTechStacks(@Param("id") Long id);

    // 이력서용 문장 자동 변환
    @Transactional
    public ResumeConvertResponse convertToResume(Long memberId, Long projectId) {

        // 1. 프로젝트 데이터 수집(프로젝트 + 기술 스택) + 본인 소유 검증
//        Project project = projectRepository.findByIdWithTechStacks(projectId)
//                .orElseThrow(() -> new ProjectNotFoundException(projectId));
//        if (!project.isOwnedBy(memberId) {
//            throw new ProjectAccessDeniedException();
//        }

        // 2. AI 기능 사용 가능 여부 검증 (구독 + 토큰 한도)
        UsageSnapshot snapshot = validateAndGetUsage(memberId);

        // 3. LLM에 보낼 텍스트 전처리 (StringBuilder 힙 메모리 최적화)
        String userInput = buildResumeInput(project);

        // 4. LLM 호출 : ChatClient로 Gemini 호출 (메타데이터까지 받기)
        ChatResponse response = chatClientBuilder.build()
                .prompt()
                .system(AiPromptType.RESUME_CONVERT.system())
                .user(userInput)
                .call()
                .chatResponse();

        // 5. 결과 + 토큰 사용량 추출
        String resumeText = response.getResult().getOutput().getText();
        Usage usage = response.getMetadata().getUsage();
        int inputTokens = usage.getPromptTokens();
        int outputTokens = usage.getCompletionTokens();

        // 6. AI API 호출 결과 사용량 기록 + 응답 조립
        TokenUsageInfo usageInfo = recordUsageAndCalculate(
                memberId, "RESUME_CONVERT", snapshot, inputTokens, outputTokens);

        return new ResumeConvertResponse(
                resumeText, usageInfo.daily(), usageInfo.monthly());
    }

    // 이력서용 LLM에 보낼 텍스트 전처리
    // 프로젝트 데이터를 LLM이 이해하기 좋은 텍스트로 조립
    private String buildResumeInput(Project project) {
        StringBuilder sb = new StringBuilder();

//     프로젝트 명
        sb.append("프로젝트명: ").append(project.getTitle()).append("\n");

//     설명
            sb.append("설명: ").append(project.getDescription()).append("\n");

        // 기간 정보 (둘 다 있을 때만)
        if (project.getStartDate() != null && project.getEndDate() != null) {
            sb.append("기간: ")
                    .append(project.getStartDate()).append(" ~ ").append(project.getEndDate())
                    .append("\n");
        }

        // 협업 정보 (isTeam이 boolean 원시 타입이므로 if-else로 처리)
        if (project.isTeam()) {
            sb.append("진행 방식: 팀 프로젝트");
            if (project.getTeamSize() != null) {
                sb.append(" (").append(project.getTeamSize()).append("명)");
            }
            sb.append("\n");

            if (project.getMyRole() != null && !project.getMyRole().isBlank()) {
                sb.append("담당 역할: ").append(project.getMyRole()).append("\n");
            }
        } else if (Boolean.FALSE.equals(project.getIsTeam())) {
            sb.append("진행 방식: 개인 프로젝트\n");
        }

    // 기술 스택
    // findByIdWithTechStacks에서 JPQL쿼리을 이용하여 프로젝트조회시 한번에 같이 가져옴)
    List<ProjectTech> techStacks = project.getTechStacks();
        if (techStacks != null && !techStacks.isEmpty()) {
        String techNames = techStacks.stream()
                .map(ProjectTech::getTechName)
                .collect(Collectors.joining(", "));
        sb.append("사용 기술: ").append(techNames).append("\n");
    }

        return sb.toString();
    }

    //===================================================================================================//

    // 포트폴리오 피드백용 (회원의 모든 프로젝트 + 각 기술 스택), 최근 N개 조회
//    @Query("SELECT p FROM Project p " +
//            "LEFT JOIN FETCH p.techStacks " +
//            "WHERE p.memberId = :memberId " +
//            "ORDER BY p.createdAt DESC")
//    List<Project> findAllByMemberIdWithTechStacks(@Param("memberId") Long memberId, Pageable pageable);

    // 포트폴리오 피드백용, 최근 N개 조회
//    @Query("SELECT f FROM Feed f WHERE f.user.id = :userId " +
//            "ORDER BY f.createdAt DESC")
//    List<Feed> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
    // 프토폴리오 피드백
    @Transactional
    public PortfolioFeedbackResponse generatePortfolioFeedback(
            Long memberId) {

        // 1. AI 기능 사용 가능 여부 검증 (구독 + 토큰 한도)
        UsageSnapshot snapshot = validateAndGetUsage(memberId);

        // 2. 데이터 수집 - 4개의 쿼리
        // 1차캐시에서 사용자 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // 사용자 기술스택 조회
        List<UserTechStack> userTechStacks =
                userTechStackRepository.findByMemberId(memberId);
//        userTechStackRepository.findByUserId(memberId);?

        // 사용자 프로젝트 + 프로젝트별 기술스택 조회
        // 최신 프로젝트 10개만 가져옴
        List<Project> projects =
                projectRepository.findAllByMemberIdWithTechStacks(memberId, PageRequest.of(0, 10));

        // 프로젝트 존재 여부 검증 : 프로젝트가 0개인 경우 피드백 생성 불가
        if (projects == null || projects.isEmpty()) {
            throw new ProjectNotFoundException();
        }

        // 사용자 피드조회
        // 최신 피드 20개만 가져옴
        List<Feed> feeds =
                feedRepository.findRecentByUserId(memberId, PageRequest.of(0, 20));


        // 3. LLM에 보낼 텍스트 전처리 (StringBuilder 힙 메모리 최적화)
        String portfolioText = buildPortfolioText(
                member, userTechStacks, projects, feeds);

        // 4. LLM 호출 : ChatClient로 Gemini 호출 (메타데이터까지 받기), JSON 스트링 획득
        ChatResponse response = chatClientBuilder.build()
                .prompt()
                .system(AiPromptType.PORTFOLIO_FEEDBACK.system())
                .user(portfolioText)
                .call()
                .chatResponse();

        // 5. AI API 원본 결과 텍스트 추출 (JSON 문자열)
        String rawJsonResponse = response.getResult().getOutput().getText();

        // 6. 토큰 사용량 추출
        Usage usage = response.getMetadata().getUsage();
        int inputTokens = usage.getPromptTokens();
        int outputTokens = usage.getCompletionTokens();

        // 7. 데이터 정제(Cleansing) 및 자바 객체 바인딩(Parsing)
        FeedbackContentDto feedbackContent;
        try {
            // 원본 문자열을 깨끗하게 정제
            String pureJson = cleanJsonResponse(rawJsonResponse);

            // LLM이 보낸 JSON 포맷 문자열을 우리가 설계한 DTO 구조체 객체로 결합
            feedbackContent = objectMapper.readValue(rawJsonResponse, FeedbackContentDto.class); // 정제된 JSON으로 파싱
        } catch (JsonProcessingException e) {
            log.error("AI 응답 JSON 구조 역직렬화 실패. 원본 데이터: {}", rawJsonResponse, e);
            throw new IllegalStateException("AI가 생성한 리포트의 규격 형식이 올바르지 않습니다. 다시 시도해 주세요.", e);
        }

        // 8. AI API 호출 결과 사용량 기록 + 응답 조립
        TokenUsageInfo usageInfo = recordUsageAndCalculate(
                memberId, "PORTFOLIO_FEEDBACK", snapshot, inputTokens, outputTokens);

        return new PortfolioFeedbackResponse(
                feedbackContent, usageInfo.daily(), usageInfo.monthly());
    }

    // 포트폴리오 피드백용 LLM에 보낼 텍스트 전처리
    // 포트폴리오 데이터를 LLM이 이해하기 좋은 텍스트로 조립
    private String buildPortfolioText(
            Member member,
            List<UserTechStack> userTechStacks,
            List<Project> projects,
            List<Feed> feeds
    ) {
        StringBuilder sb = new StringBuilder();

        // 1. 사용자 프로필
        sb.append("=== 개발자 프로필 ===\n");
        if (member.getBio() != null && !member.getBio().isBlank()) {
            sb.append("자기소개: ").append(member.getBio()).append("\n");
        }

        // 2. 사용자 보유 기술 스택
        if (!userTechStacks.isEmpty()) {
            String techList = userTechStacks.stream()
                    .map(UserTechStack::getTechName)
                    .collect(Collectors.joining(", "));
            sb.append("보유 기술: ").append(techList).append("\n");
        }
        sb.append("\n");

        // 3. 프로젝트들 (각각 기술 스택 포함)
        sb.append("=== 프로젝트 (총 ").append(projects.size()).append("개) ===\n\n");

        for (int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);
            sb.append("[").append(i + 1).append("] ").append(p.getTitle()).append("\n");
            sb.append("- 설명: ").append(p.getDescription()).append("\n");

            // fetch join으로 함께 가져온 기술 스택 사용 (추가 쿼리 X)
            if (!p.getTechStacks().isEmpty()) {
                String projectTechs = p.getTechStacks().stream()
                        .map(ProjectTech::getTechName)
                        .collect(Collectors.joining(", "));
                sb.append("- 사용 기술: ").append(projectTechs).append("\n");
            }

            if (p.getStartDate() != null && p.getEndDate() != null) {
                sb.append("- 기간: ").append(p.getStartDate())
                        .append(" ~ ").append(p.getEndDate()).append("\n");
            }
            sb.append("\n");
        }

        // 4. 피드 (최근 활동)
        if (!feeds.isEmpty()) {
            sb.append("=== 최근 활동 피드 (").append(feeds.size()).append("개) ===\n");
            for (Feed f : feeds) {
                if (f.getContent() != null && !f.getContent().isBlank()) {
                    // 너무 긴 피드는 잘라서
                    String content = f.getContent();
                    if (content.length() > 200) {
                        content = content.substring(0, 200) + "...";
                    }
                    sb.append("- ").append(content).append("\n");
                }
            }
        }

        return sb.toString();
    }

    // AI 응답 텍스트 클렌징 메서드
    private String cleanJsonResponse(String rawText) {

        if (rawText == null || rawText.isBlank()) {
            throw new IllegalArgumentException("AI 응답 데이터가 텅 비어 있습니다.");
        }

        String cleaned = rawText.trim();

        // 1. 만약 앞뒤에 마크다운 코드 블록(```json ... ```)이 붙어 있다면 완벽히 제거
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("\\s*```$", "");         // 끝 부분의 ``` 제거
            cleaned = cleaned.trim();
        }

        // 2. AI가 앞뒤로 넉살 좋게 인사말을 붙였을 경우를 대비해, 최초의 '{'와 마지막 '}'를 찾아 자름
        int firstBrace = cleaned.indexOf("{");
        int lastBrace = cleaned.lastIndexOf("}");

        if (firstBrace == -1 || lastBrace == -1 || firstBrace > lastBrace) {
            log.error("정제 실패 - 유효한 JSON 구조를 찾을 수 없음. 원본: {}", rawText);
            throw new IllegalArgumentException("AI 응답 구조가 유효한 JSON 포맷이 아닙니다.");
        }

        return cleaned.substring(firstBrace, lastBrace + 1);
    }

    //===================================================================================================//

    // AI 포트폴리오 PDF 생성
    @Transactional
    public byte[] generatePortfolioPdf(
            Long memberId) {

        // 1. AI 기능 사용 가능 여부 검증 (구독 + 토큰 한도)
        UsageSnapshot snapshot = validateAndGetUsage(memberId);

        // 2. 데이터 수집 - 4개의 쿼리
        // 1차캐시에서 사용자 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // 사용자 기술스택 조회
        List<UserTechStack> userTechStacks =
                userTechStackRepository.findByMemberId(memberId);
        //        userTechStackRepository.findByUserId(memberId);?

        // 사용자 프로젝트 + 프로젝트별 기술스택 조회
        // 최신 프로젝트 10개만 가져옴
        List<Project> projects = projectRepository
                .findAllByMemberIdWithTechStacks(memberId, PageRequest.of(0, 10));

        // 프로젝트 존재 여부 검증 : 프로젝트가 0개인 경우 피드백 생성 불가
        if (projects == null || projects.isEmpty()) {
            throw new ProjectNotFoundException();
        }

        // 사용자 피드조회
        // 최신 피드 20개만 가져옴
        List<Feed> feeds = feedRepository
                .findRecentByUserId(memberId, PageRequest.of(0, 20));

        // 3. LLM에 보낼 텍스트 조립 (포트폴리오 피드백과 동일한 메서드 재사용)
        String portfolioText = buildPortfolioText(
                member, userTechStacks, projects, feeds);

        // 4. LLM 호출 : ChatClient로 Gemini 호출 (메타데이터까지 받기), JSON 스트링 획득
        ChatResponse response = chatClientBuilder.build()
                .prompt()
                .system(AiPromptType.PORTFOLIO_PDF.system())
                .user(portfolioText)
                .call()
                .chatResponse();

        // 5. AI API 원본 결과 텍스트 추출 (JSON 문자열)
        String rawJsonResponse = response.getResult().getOutput().getText();

        // 6. 토큰 사용량 추출
        Usage usage = response.getMetadata().getUsage();
        int inputTokens = usage.getPromptTokens();
        int outputTokens = usage.getCompletionTokens();

        // 7. JSON 파싱
        PortfolioPdfContent content;
        try {
            // 원본 문자열을 깨끗하게 정제
            String pureJson = cleanJsonResponse(rawJsonResponse);
            content = objectMapper.readValue(pureJson, PortfolioPdfContent.class);
        } catch (JsonProcessingException e) {
            log.error("AI 응답 JSON 파싱 실패. 원본: {}", rawJsonResponse, e);
            throw new IllegalStateException(
                    "AI가 생성한 포트폴리오 형식이 올바르지 않습니다. 다시 시도해주세요.", e);
        }

        // 8. AI API 호출 결과 사용량 기록
        recordUsageAndCalculate(memberId, "PORTFOLIO_PDF", snapshot,
                inputTokens, outputTokens);

        // 9. HTML 렌더링
        String renderedHtml = renderPdfHtml(member, content);

        // 10. PDF 변환
        return convertHtmlToPdf(renderedHtml);
    }

    // Thymeleaf로 HTML 생성
    private String renderPdfHtml(Member member, PortfolioPdfContent content) {

        Context context = new Context();
        context.setVariable("nickname", member.getNickname());
        context.setVariable("email", member.getEmail());
        context.setVariable("refinedBio", content.refinedBio());
        context.setVariable("careerSummary", content.careerSummary());
        context.setVariable("projects", content.projects());

        return templateEngine.process("portfolio-pdf-template", context);
    }

    // HTML을 PDF 바이너리로 변환
    private byte[] convertHtmlToPdf(String html) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            // 한글 폰트 등록, 클래스패스에서 폰트 파일을 스트림으로 직접 추출
            InputStream fontStream = getClass()
                    .getResourceAsStream("/fonts/NanumGothic.ttf");
            if (fontStream == null) {
                throw new FileNotFoundException(
                        "한글 폰트 파일을 찾을 수 없습니다: /fonts/NanumGothic.ttf");
            }

            // 빌더에 폰트 스트림 주입
            builder.useFont(() -> fontStream, "NanumGothic");

            builder.withHtmlContent(html, null);
            builder.toStream(baos);
            builder.run();

            return baos.toByteArray();

        } catch (Exception e) {
            log.error("PDF 변환 실패", e);
            throw new RuntimeException("PDF 생성 중 오류 발생", e);
        }
    }

}

