package com.example.showfolio.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)   // 403으로 자동 매핑
public class ProjectAccessDeniedException extends RuntimeException {
    public ProjectAccessDeniedException() {
        super("해당 프로젝트에 대한 권한이 없습니다.");
    }
}
