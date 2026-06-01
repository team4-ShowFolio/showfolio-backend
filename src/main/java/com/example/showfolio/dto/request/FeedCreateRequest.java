package com.example.showfolio.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class FeedCreateRequest {

    private String title;

    private String content;

    @NotNull(message = "공개 범위를 선택해주세요")
    private String visibility;

    @Size(max = 5, message = "태그는 최대 5개까지 가능합니다")
    private List<String> tags = new ArrayList<>();

    @Size(max = 5, message = "이미지는 최대 5장까지 가능합니다")
    private List<String> imageUrls = new ArrayList<>();

    private List<Long> mentionedUserIds = new ArrayList<>();
}
