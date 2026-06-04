package com.example.showfolio.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponse {

    private String imageUrl;
    private String filename;

    public static ImageResponse of(String imageUrl, String filename) {
        return ImageResponse.builder()
                .imageUrl(imageUrl)
                .filename(filename)
                .build();
    }
}
