package com.example.showfolio.exception;

public class ProjectAccessDeniedException extends RuntimeException {
    public ProjectAccessDeniedException() {
        super("본인의 프로젝트만 변환할 수 있습니다.");
    }
}