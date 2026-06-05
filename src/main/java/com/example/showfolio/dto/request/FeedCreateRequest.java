package com.example.showfolio.dto.request;

import com.example.showfolio.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class FeedCreateRequest {

    @NotBlank(message = "피드 제목을 입력해주세요") //제목 공백 방지
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    private String title;

    private String content;

    @NotNull(message = "공개 범위를 선택해주세요")
    private Visibility visibility;

    // 프로젝트 연결
    private Long projectId;

    @Size(max = 5, message = "태그는 최대 5개까지 가능합니다")
    private List<String> tags = new ArrayList<>();

    @Size(max = 5, message = "이미지는 최대 5장까지 가능합니다")
    private List<String> imageUrls = new ArrayList<>();

    private List<Long> mentionedUserIds = new ArrayList<>();
}
