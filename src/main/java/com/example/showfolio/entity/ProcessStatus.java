package com.example.showfolio.entity;

import lombok.Getter;

@Getter
public enum ProcessStatus {
    
    PENDING("검토 중"),
    PROCESSED("처리 완료"),
    REJECTED("반려");

    private final String statusName;

    ProcessStatus(String statusName) {
        this.statusName = statusName;
    }
}
