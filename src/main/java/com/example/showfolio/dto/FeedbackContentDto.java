package com.example.showfolio.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // LLM에 의해 추가된 필드 무시
public record FeedbackContentDto(
        List<String> strengths,                             // 강점 목록 (3개 내외)
        List<ImprovementElement> improvements,              // 개선점 목록 (3개 내외)
        List<TechElement> recommendedTechnologies,          // 추천 기술 목록 (2-3개)
        List<CompanyElement> recommendedCompanies,          // 추천 회사 목록 (2-3개)
        List<InterviewQuestionElement> interviewQuestions   // 면접 질문 목록 (5개)
) {
    public record ImprovementElement(
            String issue,                                   // 보완이 필요한 부분
            String direction                                // 구체적인 개선 방향
    ) {}

    public record TechElement(
            String name,                                    // 기술명 (예: Redis, Kafka)
            String reason                                   // 추천 이유
    ) {}

    public record CompanyElement(
            String type,                                    // 회사 유형 (예: B2B SaaS 스타트업)
            String reason,                                  // 구체적 근거
            List<String> examples                           // 한국 회사 예시 리스트
    ) {}

    public record InterviewQuestionElement(
            int number,                                     // 질문 번호
            String question,                                // 질문 내용
            String intention,                               // 출제 의도
            String evidence,                                // 서류상 근거 레코드
            List<String> followUp                           // 예상 꼬리 질문 목록
    ) {}
}