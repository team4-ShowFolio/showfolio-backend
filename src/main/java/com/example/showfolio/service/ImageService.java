package com.example.showfolio.service;

import com.example.showfolio.dto.response.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 이미지 업로드
    public ImageResponse uploadImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 없습니다");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 10MB 이하만 가능합니다");
        }


        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename
                .substring(originalFilename.lastIndexOf("."));

        String savedFilename = UUID.randomUUID().toString() + extension;

        if (uploadDir == null || uploadDir.isBlank()) {
            throw new IllegalStateException("이미지 업로드 경로(UPLOAD_DIR)가 설정되어 있지 않습니다");
        }
        String cleanUploadDir = uploadDir.trim();

        if (!cleanUploadDir.endsWith("/") && !cleanUploadDir.endsWith("\\")) {
            cleanUploadDir += File.separator; // 디렉터리 구분 문자 자동 추가
        }

        String savePath = cleanUploadDir + savedFilename;

        try {
            // 폴더 없으면 생성
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file.transferTo(new File(savePath));

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패했습니다");
        }

        String imageUrl = "/uploads/" + savedFilename;

        return ImageResponse.of(imageUrl, savedFilename);
    }

    public void deleteImage(String filename) {

        String filePath = uploadDir + filename;
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
    }
}
