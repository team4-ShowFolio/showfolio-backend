package com.example.showfolio.controller;

import com.example.showfolio.dto.response.ImageResponse;
import com.example.showfolio.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(imageService.uploadImage(file));
    }

    // 이미지 삭제
    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable String filename,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());

        imageService.deleteImage(filename);
        return ResponseEntity.noContent().build();
    }
}
