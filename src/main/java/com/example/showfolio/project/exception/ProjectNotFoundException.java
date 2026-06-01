package com.example.showfolio.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)   // 404로 자동 매핑
public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException() {
        super("프로젝트를 찾을 수 없습니다.");
    }
}
