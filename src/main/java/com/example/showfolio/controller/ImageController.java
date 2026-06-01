package com.example.showfolio.controller;

import com.example.showfolio.dto.response.ImageResponse;
import com.example.showfolio.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // 이미지 업로드
    @PostMapping("/upload")
    public ResponseEntity<ImageResponse> uploadImage(
            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(imageService.uploadImage(file));
    }

    // 이미지 삭제
    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> deleteImage(
            // JWT 완성 후 @AuthenticationPrincipal Long currentUserId로 교체
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable String filename) {

        imageService.deleteImage(filename);
        return ResponseEntity.noContent().build();
    }
}
