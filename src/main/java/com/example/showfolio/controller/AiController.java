package com.example.showfolio.controller;

import com.example.showfolio.dto.DescriptionImproveRequest;
import com.example.showfolio.dto.DescriptionImproveResponse;
import com.example.showfolio.dto.PortfolioFeedbackResponse;
import com.example.showfolio.dto.ResumeConvertResponse;
import com.example.showfolio.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
//    private final JwtUtil jwtUtil;

    // 프로젝트 설명 개선 by AI
    // 프로젝트 등록 페이지에서 [프로젝트 설명 개선] 버튼 클릭 시 호출.
    @PostMapping("/improve/description")
    public ResponseEntity<DescriptionImproveResponse> improveDescription(
//            @RequestHeader("Authorization") String token,
            @Valid @RequestBody DescriptionImproveRequest request) {

        // TODO: 인증 통합 후 실제 회원 ID 추출 로직으로 교체
        Long memberId = 1L;  // 임시값. 테스트 진행용

//        Long memberId = jwtUtil.getUserId(token.substring(7));

        return ResponseEntity.ok(aiService.improveDescription(memberId, request));
    }

    // 이력서용 문장 자동 변환 by AI
    // 프로젝트 상세 페이지에서 [이력서 문장으로 변환] 버튼 클릭 시 호출.
    // 백엔드가 projectId로 DB에서 프로젝트 정보를 조회해 AI 변환.
    @PostMapping("/convert/resume/{projectId}")
    public ResponseEntity<ResumeConvertResponse> convertToResume(
//            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId) {

        // TODO: 인증 통합 후 실제 회원 ID 추출 로직으로 교체
        Long memberId = 1L;  // 임시값. 테스트 진행용

//        Long memberId = jwtUtil.getUserId(token.substring(7));

        return ResponseEntity.ok(aiService.convertToResume(memberId, projectId));
    }

    // 포트폴리오 피드백 by AI
    // 마이페이지에서 [AI 포트폴리오 피드백] 버튼 클릭 시 호출.
    @PostMapping("/portfolio-feedback")
    public ResponseEntity<PortfolioFeedbackResponse> portfolioFeedback(
//            @RequestHeader("Authorization") String token
    ) {

        // TODO: 인증 통합 후 실제 회원 ID 추출 로직으로 교체
        Long memberId = 1L;  // 임시값. 테스트 진행용

//        Long memberId = jwtUtil.getUserId(token.substring(7));

        return ResponseEntity.ok(aiService.generatePortfolioFeedback(memberId));
    }

    // AI 포트폴리오 PDF 다운로드
    // 마이페이지에서 [PDF 포트폴리오 자동 생성] 버튼 클릭 시 호출.
    // 사용자의 모든 데이터를 AI로 정제 → HTML 렌더링 → PDF 변환 → 다운로드
    @PostMapping("/portfolio-pdf")
    public ResponseEntity<byte[]> downloadPortfolioPdf(
//            @RequestHeader("Authorization") String token
    ) {

//        Long memberId = jwtUtil.getUserId(token.substring(7));

        // TODO: 인증 통합 후 실제 회원 ID 추출 로직으로 교체
        Long memberId = 1L;  // 임시값. 테스트 진행용

        byte[] pdfBytes = aiService.generatePortfolioPdf(memberId);

        // 프론트엔드로 파일 다운로드 헤더와 함께 바이너리 전송
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("portfolio.pdf", StandardCharsets.UTF_8)
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
