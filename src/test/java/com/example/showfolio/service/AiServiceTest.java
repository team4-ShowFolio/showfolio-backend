package com.example.showfolio.service;

import com.example.showfolio.dto.FeedbackContentDto;
import com.example.showfolio.dto.PortfolioPdfContentDto;
import com.example.showfolio.dto.TokenUsage;
import com.example.showfolio.dto.request.DescriptionImproveRequest;
import com.example.showfolio.dto.response.DescriptionImproveResponse;
import com.example.showfolio.dto.response.PortfolioFeedbackResponse;
import com.example.showfolio.dto.response.ResumeConvertResponse;
import com.example.showfolio.entity.*;
import com.example.showfolio.enums.SubscriptionType;
import com.example.showfolio.enums.Visibility;
import com.example.showfolio.exception.*;
import com.example.showfolio.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * AiService 단위 테스트
 *
 * 테스트 전략:
 * - Spring Context 없이 Mockito만으로 순수 단위 테스트
 * - 각 public 메서드의 정상 흐름(Happy Path)과 예외 흐름(Sad Path)을 분리
 * - @Nested로 메서드별 테스트 그룹화
 *
 * 테스트 대상 메서드:
 * 1. improveDescription        - 프로젝트 설명 AI 개선
 * 2. convertToResume           - 이력서용 문장 자동 변환
 * 3. generatePortfolioFeedback - 포트폴리오 피드백
 * 4. generatePortfolioPdf      - AI 포트폴리오 PDF 생성
 *
 * 공통 검증 시나리오 (validateAndGetUsage):
 * - 비프리미엄 회원 접근 차단
 * - 일일 토큰 한도 초과 차단
 * - 월별 토큰 한도 초과 차단
 */
@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    // ── Mocks ──────────────────────────────────────────────────────────────────

    @Mock private ChatClient chatClient;
    @Mock private AiUsageRepository aiUsageRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserTechStackRepository userTechStackRepository;
    @Mock private FeedRepository feedRepository;
    @Mock private TemplateEngine templateEngine;

    // ChatClient 체이닝 모킹에 필요한 중간 객체들
    @Mock private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock private ChatClient.CallResponseSpec callResponseSpec;
    @Mock private ChatResponse chatResponse;
    @Mock private Generation generation;
    @Mock private AssistantMessage assistantMessage;
    @Mock private Usage usage;
    @Mock private org.springframework.ai.chat.metadata.ChatResponseMetadata chatResponseMetadata;

    @InjectMocks
    private AiService aiService;

    // ── 상수 ───────────────────────────────────────────────────────────────────

    private static final Long MEMBER_ID  = 1L;
    private static final Long PROJECT_ID = 10L;

    // ── 픽스처 ─────────────────────────────────────────────────────────────────

    private Member premiumMember;
    private Member freeMember;
    private Project sampleProject;

    @BeforeEach
    void setUp() {
        premiumMember = buildPremiumMember();
        freeMember    = buildFreeMember();
        sampleProject = buildProject();
    }

    // ── 픽스처 빌더 ────────────────────────────────────────────────────────────

    private Member buildPremiumMember() {
        Member m = new Member();
        m.setId(MEMBER_ID);
        m.setEmail("premium@test.com");
        m.setNickname("tester");
        m.setBio("백엔드 개발자입니다.");
        m.setSubscriptionType(SubscriptionType.PREMIUM);
        m.setSubscriptionExpiredAt(LocalDateTime.now().plusDays(30));
        return m;
    }

    private Member buildFreeMember() {
        Member m = new Member();
        m.setId(MEMBER_ID);
        m.setEmail("free@test.com");
        m.setNickname("free-tester");
        m.setSubscriptionType(SubscriptionType.FREE);
        return m;
    }

    private Project buildProject() {
        return Project.builder()
                .id(PROJECT_ID)
                .memberId(MEMBER_ID)
                .title("ShowFolio")
                .description("개발자 포트폴리오 공유 플랫폼")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .isTeam(true)
                .teamSize(4)
                .myRole("백엔드 개발")
                .visibility(Visibility.PUBLIC)
                .build();
    }

    private UserTechStack buildUserTechStack(String techName) {
        UserTechStack uts = new UserTechStack();
        uts.setTechName(techName);
        uts.setMember(premiumMember);
        return uts;
    }

    private Feed buildFeed(String content) {
        return Feed.builder()
                .member(premiumMember)
                .title("피드 제목")
                .content(content)
                .visibility(Visibility.PUBLIC)
                .build();
    }

    /**
     * ChatClient의 체이닝 호출(prompt→system→user→call→chatResponse)을 한 번에 스텁
     */
    private void stubChatClient(String responseText, int inputTokens, int outputTokens) {
        given(chatClient.prompt()).willReturn(requestSpec);
        given(requestSpec.system(anyString())).willReturn(requestSpec);
        given(requestSpec.user(anyString())).willReturn(requestSpec);
        given(requestSpec.call()).willReturn(callResponseSpec);
        given(callResponseSpec.chatResponse()).willReturn(chatResponse);

        given(chatResponse.getResult()).willReturn(generation);
        given(generation.getOutput()).willReturn(assistantMessage);
        given(assistantMessage.getText()).willReturn(responseText);

        given(chatResponse.getMetadata()).willReturn(chatResponseMetadata);
        given(chatResponseMetadata.getUsage()).willReturn(usage);
        given(usage.getPromptTokens()).willReturn(inputTokens);
        given(usage.getCompletionTokens()).willReturn(outputTokens);
    }

    /**
     * validateAndGetUsage 통과를 위한 공통 스텁
     * - premiumMember 반환
     * - 당일/당월 사용량 0 반환
     */
    private void stubValidationPass() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
        given(aiUsageRepository.sumTokensSince(eq(MEMBER_ID), any(LocalDateTime.class))).willReturn(0);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 1. improveDescription
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("improveDescription - 프로젝트 설명 AI 개선")
    class ImproveDescriptionTest {

        private final DescriptionImproveRequest REQUEST =
                new DescriptionImproveRequest("기존 설명 텍스트");

        @Test
        @DisplayName("정상 흐름: 개선된 설명과 토큰 사용량 정보가 반환된다")
        void success() {
            // given
            stubValidationPass();
            stubChatClient("개선된 설명 텍스트입니다.", 100, 50);
            given(aiUsageRepository.save(any(AiUsage.class))).willReturn(null);

            // when
            DescriptionImproveResponse response =
                    aiService.improveDescription(MEMBER_ID, REQUEST);

            // then
            assertThat(response.improvedDescription()).isEqualTo("개선된 설명 텍스트입니다.");

            TokenUsage.Daily daily = response.daily();
            assertThat(daily.todayUsed()).isEqualTo(150);     // 100 + 50
            assertThat(daily.dailyLimit()).isEqualTo(100_000);
            assertThat(daily.remaining()).isEqualTo(99_850);  // 100_000 - 150

            TokenUsage.Monthly monthly = response.monthly();
            assertThat(monthly.monthUsed()).isEqualTo(150);
            assertThat(monthly.monthlyLimit()).isEqualTo(2_000_000);
            assertThat(monthly.remaining()).isEqualTo(1_999_850);

            verify(aiUsageRepository).save(any(AiUsage.class));
        }

        @Test
        @DisplayName("예외 흐름: 존재하지 않는 회원이면 MemberNotFoundException이 발생한다")
        void failWhenMemberNotFound() {
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> aiService.improveDescription(MEMBER_ID, REQUEST))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("예외 흐름: 비프리미엄 회원은 PremiumRequiredException이 발생한다")
        void failWhenNotPremium() {
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(freeMember));

            assertThatThrownBy(() -> aiService.improveDescription(MEMBER_ID, REQUEST))
                    .isInstanceOf(PremiumRequiredException.class);
        }

        @Test
        @DisplayName("예외 흐름: 일일 토큰 한도 초과 시 DailyLimitExceededException이 발생한다")
        void failWhenDailyLimitExceeded() {
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            // 첫 번째 sumTokensSince(당일) 호출에서 한도값 반환
            given(aiUsageRepository.sumTokensSince(eq(MEMBER_ID), any(LocalDateTime.class)))
                    .willReturn(100_000);

            assertThatThrownBy(() -> aiService.improveDescription(MEMBER_ID, REQUEST))
                    .isInstanceOf(DailyLimitExceededException.class);
        }

        @Test
        @DisplayName("예외 흐름: 월별 토큰 한도 초과 시 MonthlyLimitExceededException이 발생한다")
        void failWhenMonthlyLimitExceeded() {
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            // 첫 번째(당일) → 통과, 두 번째(당월) → 한도 초과
            given(aiUsageRepository.sumTokensSince(eq(MEMBER_ID), any(LocalDateTime.class)))
                    .willReturn(0)           // todayUsed: 통과
                    .willReturn(2_000_000);  // monthUsed: 초과

            assertThatThrownBy(() -> aiService.improveDescription(MEMBER_ID, REQUEST))
                    .isInstanceOf(MonthlyLimitExceededException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 2. convertToResume
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("convertToResume - 이력서용 문장 자동 변환")
    class ConvertToResumeTest {

        @Test
        @DisplayName("정상 흐름: 이력서 텍스트와 토큰 사용량 정보가 반환된다")
        void success() {
            // given
            given(projectRepository.findByIdWithTechStacks(PROJECT_ID))
                    .willReturn(Optional.of(sampleProject));
            stubValidationPass();
            stubChatClient("이력서 변환 결과 텍스트", 200, 100);
            given(aiUsageRepository.save(any(AiUsage.class))).willReturn(null);

            // when
            ResumeConvertResponse response = aiService.convertToResume(MEMBER_ID, PROJECT_ID);

            // then
            assertThat(response.resumeText()).isEqualTo("이력서 변환 결과 텍스트");
            assertThat(response.daily().todayUsed()).isEqualTo(300);     // 200 + 100
            assertThat(response.daily().dailyLimit()).isEqualTo(100_000);
            assertThat(response.monthly().monthUsed()).isEqualTo(300);
            verify(aiUsageRepository).save(any(AiUsage.class));
        }

        @Test
        @DisplayName("정상 흐름: 개인 프로젝트(isTeam=false)도 정상 변환된다")
        void successWithSoloProject() {
            // given
            Project soloProject = Project.builder()
                    .id(PROJECT_ID)
                    .memberId(MEMBER_ID)
                    .title("개인 사이드 프로젝트")
                    .description("혼자 만든 프로젝트")
                    .isTeam(false)
                    .visibility(Visibility.PUBLIC)
                    .build();

            given(projectRepository.findByIdWithTechStacks(PROJECT_ID))
                    .willReturn(Optional.of(soloProject));
            stubValidationPass();
            stubChatClient("이력서 결과", 50, 50);
            given(aiUsageRepository.save(any(AiUsage.class))).willReturn(null);

            // when
            ResumeConvertResponse response = aiService.convertToResume(MEMBER_ID, PROJECT_ID);

            // then
            assertThat(response.resumeText()).isEqualTo("이력서 결과");
            assertThat(response.daily().todayUsed()).isEqualTo(100);
        }

        @Test
        @DisplayName("예외 흐름: 존재하지 않는 프로젝트이면 ProjectNotFoundException이 발생한다")
        void failWhenProjectNotFound() {
            given(projectRepository.findByIdWithTechStacks(PROJECT_ID))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> aiService.convertToResume(MEMBER_ID, PROJECT_ID))
                    .isInstanceOf(ProjectNotFoundException.class);
        }

        @Test
        @DisplayName("예외 흐름: 다른 회원의 프로젝트 접근 시 ProjectAccessDeniedException이 발생한다")
        void failWhenNotOwner() {
            Project otherProject = Project.builder()
                    .id(PROJECT_ID)
                    .memberId(999L)  // 다른 회원 소유
                    .title("타인의 프로젝트")
                    .visibility(Visibility.PUBLIC)
                    .build();

            given(projectRepository.findByIdWithTechStacks(PROJECT_ID))
                    .willReturn(Optional.of(otherProject));

            assertThatThrownBy(() -> aiService.convertToResume(MEMBER_ID, PROJECT_ID))
                    .isInstanceOf(ProjectAccessDeniedException.class);
        }

        @Test
        @DisplayName("예외 흐름: 비프리미엄 회원은 PremiumRequiredException이 발생한다")
        void failWhenNotPremium() {
            given(projectRepository.findByIdWithTechStacks(PROJECT_ID))
                    .willReturn(Optional.of(sampleProject));
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(freeMember));

            assertThatThrownBy(() -> aiService.convertToResume(MEMBER_ID, PROJECT_ID))
                    .isInstanceOf(PremiumRequiredException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 3. generatePortfolioFeedback
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("generatePortfolioFeedback - 포트폴리오 AI 피드백")
    class GeneratePortfolioFeedbackTest {

        /**
         * FeedbackContentDto의 실제 record 구조에 맞춘 JSON 픽스처
         *
         * improvements         → List<ImprovementElement>  { issue, direction }
         * recommendedTechnologies → List<TechElement>      { name, reason }
         * recommendedCompanies → List<CompanyElement>       { type, reason, examples }
         * interviewQuestions   → List<InterviewQuestionElement> { number, question, intention, evidence, followUp }
         */
        private static final String VALID_JSON_RESPONSE = """
                {
                  "strengths": ["다양한 기술 스택 보유", "포트폴리오 활동 꾸준함"],
                  "improvements": [
                    { "issue": "프로젝트 설명 부족", "direction": "정량적 성과 중심으로 재작성" }
                  ],
                  "recommendedTechnologies": [
                    { "name": "Redis", "reason": "캐싱 경험이 없어 성능 최적화 역량 보완 필요" }
                  ],
                  "recommendedCompanies": [
                    {
                      "type": "B2B SaaS 스타트업",
                      "reason": "Spring 기반 백엔드 역량과 잘 맞음",
                      "examples": ["토스", "당근마켓"]
                    }
                  ],
                  "interviewQuestions": [
                    {
                      "number": 1,
                      "question": "JPA N+1 문제를 어떻게 해결하셨나요?",
                      "intention": "ORM 이해도 확인",
                      "evidence": "ShowFolio 프로젝트 fetch join 사용",
                      "followUp": ["Batch Size 설정은?", "EntityGraph와의 차이는?"]
                    }
                  ]
                }
                """;

        @Test
        @DisplayName("정상 흐름: 피드백 DTO와 토큰 사용량 정보가 올바르게 반환된다")
        void success() {
            // given
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember))
                    .willReturn(List.of(buildUserTechStack("Java"), buildUserTechStack("Spring")));
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of(sampleProject));
            given(feedRepository.findByMember(eq(premiumMember), any()))
                    .willReturn(List.of(buildFeed("Spring AI 공부 중")));
            stubChatClient(VALID_JSON_RESPONSE, 500, 300);
            given(aiUsageRepository.save(any(AiUsage.class))).willReturn(null);

            // when
            PortfolioFeedbackResponse response = aiService.generatePortfolioFeedback(MEMBER_ID);

            // then — PortfolioFeedbackResponse.feedback() 필드명 확인
            FeedbackContentDto feedback = response.feedback();
            assertThat(feedback.strengths())
                    .containsExactly("다양한 기술 스택 보유", "포트폴리오 활동 꾸준함");

            assertThat(feedback.improvements()).hasSize(1);
            assertThat(feedback.improvements().get(0).issue()).isEqualTo("프로젝트 설명 부족");
            assertThat(feedback.improvements().get(0).direction()).isEqualTo("정량적 성과 중심으로 재작성");

            assertThat(feedback.recommendedTechnologies()).hasSize(1);
            assertThat(feedback.recommendedTechnologies().get(0).name()).isEqualTo("Redis");

            assertThat(feedback.recommendedCompanies()).hasSize(1);
            assertThat(feedback.recommendedCompanies().get(0).type()).isEqualTo("B2B SaaS 스타트업");
            assertThat(feedback.recommendedCompanies().get(0).examples())
                    .containsExactly("토스", "당근마켓");

            assertThat(feedback.interviewQuestions()).hasSize(1);
            FeedbackContentDto.InterviewQuestionElement iq = feedback.interviewQuestions().get(0);
            assertThat(iq.number()).isEqualTo(1);
            assertThat(iq.question()).isEqualTo("JPA N+1 문제를 어떻게 해결하셨나요?");
            assertThat(iq.followUp()).containsExactly("Batch Size 설정은?", "EntityGraph와의 차이는?");

            // 토큰 사용량 검증
            assertThat(response.daily().todayUsed()).isEqualTo(800);      // 500 + 300
            assertThat(response.daily().dailyLimit()).isEqualTo(100_000);
            assertThat(response.daily().remaining()).isEqualTo(99_200);
            assertThat(response.monthly().monthUsed()).isEqualTo(800);

            verify(aiUsageRepository).save(any(AiUsage.class));
        }

        @Test
        @DisplayName("정상 흐름: LLM 응답이 ```json 마크다운으로 감싸여도 정상 파싱된다")
        void successWithMarkdownWrappedJson() {
            // given
            String markdownWrapped = "```json\n" + VALID_JSON_RESPONSE + "\n```";
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of(sampleProject));
            given(feedRepository.findByMember(eq(premiumMember), any())).willReturn(List.of());
            stubChatClient(markdownWrapped, 100, 100);
            given(aiUsageRepository.save(any(AiUsage.class))).willReturn(null);

            // when & then
            assertThatCode(() -> aiService.generatePortfolioFeedback(MEMBER_ID))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("예외 흐름: 프로젝트가 0개이면 ProjectNotFoundException이 발생한다")
        void failWhenNoProjects() {
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of());

            assertThatThrownBy(() -> aiService.generatePortfolioFeedback(MEMBER_ID))
                    .isInstanceOf(ProjectNotFoundException.class);
        }

        @Test
        @DisplayName("예외 흐름: LLM이 빈 문자열을 반환하면 AiResponseFormatException이 발생한다")
        void failWhenEmptyLlmResponse() {
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of(sampleProject));
            given(feedRepository.findByMember(eq(premiumMember), any())).willReturn(List.of());
            stubChatClient("", 0, 0);

            assertThatThrownBy(() -> aiService.generatePortfolioFeedback(MEMBER_ID))
                    .isInstanceOf(AiResponseFormatException.class);
        }

        @Test
        @DisplayName("예외 흐름: LLM이 JSON 형식이 아닌 일반 텍스트를 반환하면 AiResponseFormatException이 발생한다")
        void failWhenInvalidJsonResponse() {
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of(sampleProject));
            given(feedRepository.findByMember(eq(premiumMember), any())).willReturn(List.of());
            stubChatClient("이것은 JSON이 아닙니다.", 50, 50);

            assertThatThrownBy(() -> aiService.generatePortfolioFeedback(MEMBER_ID))
                    .isInstanceOf(AiResponseFormatException.class);
        }

        @Test
        @DisplayName("예외 흐름: strengths 필드가 누락된 JSON이면 AiResponseFormatException이 발생한다")
        void failWhenMissingStrengthsField() {
            // strengths 필드 누락
            String incompleteJson = """
                    {
                      "improvements": [{ "issue": "이슈", "direction": "방향" }],
                      "recommendedTechnologies": [],
                      "recommendedCompanies": [],
                      "interviewQuestions": []
                    }
                    """;
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of(sampleProject));
            given(feedRepository.findByMember(eq(premiumMember), any())).willReturn(List.of());
            stubChatClient(incompleteJson, 100, 100);

            assertThatThrownBy(() -> aiService.generatePortfolioFeedback(MEMBER_ID))
                    .isInstanceOf(AiResponseFormatException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 4. generatePortfolioPdf
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("generatePortfolioPdf - AI 포트폴리오 PDF 생성")
    class GeneratePortfolioPdfTest {

        private static final String VALID_PDF_JSON = """
                {
                  "refinedBio": "정제된 자기소개입니다.",
                  "careerSummary": "Spring Boot 기반 백엔드 개발 경험 보유",
                  "projects": [
                    {
                      "title": "ShowFolio",
                      "description": "개발자 포트폴리오 플랫폼",
                      "techStacks": ["Java", "Spring Boot", "MySQL"],
                      "period": "2024.01 ~ 2024.06",
                      "role": "백엔드 개발"
                    }
                  ]
                }
                """;

        @Test
        @DisplayName("정상 흐름: PDF 바이너리 데이터가 반환된다")
        void success() {
            // given
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of(sampleProject));
            given(feedRepository.findByMember(eq(premiumMember), any())).willReturn(List.of());
            stubChatClient(VALID_PDF_JSON, 600, 400);

            // Thymeleaf 렌더링 → openhtmltopdf가 처리할 수 있는 최소 HTML
            String minimalHtml = "<html><body><p>포트폴리오</p></body></html>";
            given(templateEngine.process(eq("portfolio-pdf-template"), any(Context.class)))
                    .willReturn(minimalHtml);
            given(aiUsageRepository.save(any(AiUsage.class))).willReturn(null);

            // when
            byte[] pdf = aiService.generatePortfolioPdf(MEMBER_ID);

            // then
            assertThat(pdf).isNotNull().isNotEmpty();
            verify(aiUsageRepository).save(any(AiUsage.class));
        }

        @Test
        @DisplayName("예외 흐름: 프로젝트가 0개이면 ProjectNotFoundException이 발생한다")
        void failWhenNoProjects() {
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of());

            assertThatThrownBy(() -> aiService.generatePortfolioPdf(MEMBER_ID))
                    .isInstanceOf(ProjectNotFoundException.class);
        }

        @Test
        @DisplayName("예외 흐름: AI 응답의 refinedBio가 null이면 AiResponseFormatException이 발생한다")
        void failWhenRefinedBioIsNull() {
            String missingBioJson = """
                    {
                      "careerSummary": "경력 요약",
                      "projects": [{ "title": "프로젝트" }]
                    }
                    """;
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of(sampleProject));
            given(feedRepository.findByMember(eq(premiumMember), any())).willReturn(List.of());
            stubChatClient(missingBioJson, 100, 100);

            assertThatThrownBy(() -> aiService.generatePortfolioPdf(MEMBER_ID))
                    .isInstanceOf(AiResponseFormatException.class);
        }

        @Test
        @DisplayName("예외 흐름: AI 응답의 projects가 빈 배열이면 AiResponseFormatException이 발생한다")
        void failWhenProjectsEmpty() {
            String emptyProjectsJson = """
                    {
                      "refinedBio": "자기소개",
                      "careerSummary": "경력",
                      "projects": []
                    }
                    """;
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of(sampleProject));
            given(feedRepository.findByMember(eq(premiumMember), any())).willReturn(List.of());
            stubChatClient(emptyProjectsJson, 100, 100);

            assertThatThrownBy(() -> aiService.generatePortfolioPdf(MEMBER_ID))
                    .isInstanceOf(AiResponseFormatException.class);
        }

        @Test
        @DisplayName("경계값: PDF 변환 실패 시 사용량이 기록되지 않는다 (토큰 차감 방지)")
        void usageNotRecordedWhenPdfFails() {
            // given: templateEngine이 예외를 던져 renderPdfHtml()에서 실패 유발
            //
            // [예외 전파 경로]
            // templateEngine.process() → RuntimeException("템플릿 렌더링 실패")
            //   └─ renderPdfHtml()에서 발생
            //      └─ generatePortfolioPdf()의 step 8에서 호출
            //         └─ convertHtmlToPdf()의 try-catch를 거치지 않으므로 래핑 없이 그대로 전파
            //            → 최종 메시지: "템플릿 렌더링 실패" (PDF 생성 중 오류 발생"으로 래핑되지 않음)
            stubValidationPass();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(userTechStackRepository.findByMember(premiumMember)).willReturn(List.of());
            given(projectRepository.findAllByMemberIdWithTechStacks(eq(MEMBER_ID), any()))
                    .willReturn(List.of(sampleProject));
            given(feedRepository.findByMember(eq(premiumMember), any())).willReturn(List.of());
            stubChatClient(VALID_PDF_JSON, 600, 400);
            given(templateEngine.process(anyString(), any(Context.class)))
                    .willThrow(new RuntimeException("템플릿 렌더링 실패"));

            // when & then
            assertThatThrownBy(() -> aiService.generatePortfolioPdf(MEMBER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("템플릿 렌더링 실패"); // 래핑 없이 원본 메시지 그대로 전파

            // step 10(사용량 기록)에 도달하기 전 실패했으므로 save()가 호출되면 안 됨
            verify(aiUsageRepository, never()).save(any(AiUsage.class));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 5. validateAndGetUsage - 공통 검증 로직 (경계값 테스트)
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("validateAndGetUsage - 구독·토큰 한도 공통 검증")
    class ValidateAndGetUsageTest {

        private final DescriptionImproveRequest REQUEST =
                new DescriptionImproveRequest("설명");

        @Test
        @DisplayName("경계값: 당일 사용량 99,999는 통과한다 (한도 100,000 미만)")
        void dailyLimitBoundaryPass() {
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(aiUsageRepository.sumTokensSince(eq(MEMBER_ID), any(LocalDateTime.class)))
                    .willReturn(99_999)  // 당일: 한도 1 미달 → 통과
                    .willReturn(0);      // 당월
            stubChatClient("결과", 1, 0);
            given(aiUsageRepository.save(any(AiUsage.class))).willReturn(null);

            assertThatCode(() -> aiService.improveDescription(MEMBER_ID, REQUEST))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("경계값: 당일 사용량 100,000은 차단된다 (한도와 동일)")
        void dailyLimitBoundaryBlock() {
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(aiUsageRepository.sumTokensSince(eq(MEMBER_ID), any(LocalDateTime.class)))
                    .willReturn(100_000); // 한도와 정확히 같음 → 차단

            assertThatThrownBy(() -> aiService.improveDescription(MEMBER_ID, REQUEST))
                    .isInstanceOf(DailyLimitExceededException.class);
        }

        @Test
        @DisplayName("경계값: 당월 사용량 1,999,999는 통과한다 (한도 2,000,000 미만)")
        void monthlyLimitBoundaryPass() {
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(aiUsageRepository.sumTokensSince(eq(MEMBER_ID), any(LocalDateTime.class)))
                    .willReturn(0)           // 당일: 통과
                    .willReturn(1_999_999);  // 당월: 한도 1 미달 → 통과
            stubChatClient("결과", 1, 0);
            given(aiUsageRepository.save(any(AiUsage.class))).willReturn(null);

            assertThatCode(() -> aiService.improveDescription(MEMBER_ID, REQUEST))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("경계값: 당월 사용량 2,000,000은 차단된다 (한도와 동일)")
        void monthlyLimitBoundaryBlock() {
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(premiumMember));
            given(aiUsageRepository.sumTokensSince(eq(MEMBER_ID), any(LocalDateTime.class)))
                    .willReturn(0)           // 당일: 통과
                    .willReturn(2_000_000);  // 당월: 한도와 동일 → 차단

            assertThatThrownBy(() -> aiService.improveDescription(MEMBER_ID, REQUEST))
                    .isInstanceOf(MonthlyLimitExceededException.class);
        }

        @Test
        @DisplayName("예외 흐름: 구독이 만료된 PREMIUM 회원은 PremiumRequiredException이 발생한다")
        void failWhenPremiumExpired() {
            Member expiredMember = buildPremiumMember();
            expiredMember.setSubscriptionExpiredAt(LocalDateTime.now().minusDays(1)); // 이미 만료

            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(expiredMember));

            assertThatThrownBy(() -> aiService.improveDescription(MEMBER_ID, REQUEST))
                    .isInstanceOf(PremiumRequiredException.class);
        }
    }
}