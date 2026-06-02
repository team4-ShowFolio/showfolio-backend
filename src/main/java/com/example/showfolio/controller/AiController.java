package com.example.showfolio.controller;

import com.example.showfolio.dto.DescriptionImproveRequest;
import com.example.showfolio.dto.DescriptionImproveResponse;
import com.example.showfolio.dto.PortfolioFeedbackResponse;
import com.example.showfolio.dto.ResumeConvertResponse;
import com.example.showfolio.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // 프로젝트 설명 개선 by AI
    // 프로젝트 등록 페이지에서 [프로젝트 설명 개선] 버튼 클릭 시 호출.
    @PostMapping("/improve/description")
    public ResponseEntity<DescriptionImproveResponse> improveDescription(
            // ===== 인증 통합 시 결정할 부분 =====
            // 옵션 A: Authentication 사용
            //   Authentication authentication,
            //   → Long memberId = Long.parseLong(authentication.getName());
            //
            // 옵션 B: @AuthenticationPrincipal 사용
            //   @AuthenticationPrincipal CustomUserDetails user,
            //   → Long memberId = user.getId();
            @Valid @RequestBody DescriptionImproveRequest request) {

        // TODO: 인증 통합 후 실제 회원 ID 추출 로직으로 교체
        Long memberId = 1L;  // 임시값. 테스트 진행용

        return ResponseEntity.ok(aiService.improveDescription(memberId, request));
    }

    // 이력서용 문장 자동 변환 by AI
    // 프로젝트 상세 페이지에서 [이력서 문장으로 변환] 버튼 클릭 시 호출.
    // 백엔드가 projectId로 DB에서 프로젝트 정보를 조회해 AI 변환.
    @PostMapping("/convert/resume/{projectId}")
    public ResponseEntity<ResumeConvertResponse> convertToResume(
            @PathVariable Long projectId) {

        // TODO: 인증 통합 후 실제 회원 ID 추출 로직으로 교체
        Long memberId = 1L;  // 임시값. 테스트 진행용

        return ResponseEntity.ok(aiService.convertToResume(memberId, projectId));
    }

    // 포트폴리오 피드백 by AI
    @PostMapping("/portfolio-feedback")
    public ResponseEntity<PortfolioFeedbackResponse> portfolioFeedback() {

        // TODO: 인증 통합 후 실제 회원 ID 추출 로직으로 교체
        Long memberId = 1L;  // 임시값. 테스트 진행용

        return ResponseEntity.ok(aiService.generatePortfolioFeedback(memberId));
    }
}
