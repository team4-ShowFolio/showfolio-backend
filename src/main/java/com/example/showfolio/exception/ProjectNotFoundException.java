package com.example.showfolio.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException() {
        super("최소 1개 이상의 프로젝트가 등록되어 있어야 AI 피드백을 받아볼 수 있습니다.");
    }

    public ProjectNotFoundException(Long projectId) {
        super("존재하지 않는 프로젝트입니다. id=" + projectId);
    }
}
