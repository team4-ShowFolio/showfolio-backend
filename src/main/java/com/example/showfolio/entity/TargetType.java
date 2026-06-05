package com.example.showfolio.entity;

import lombok.Getter;

/**
 * 피드, 댓글 등에서 신고 타입을 지정하기 위한 ENUM 입니다.
 *
 */
@Getter
public enum TargetType {

    FEED("피드"), COMMENT("댓글");

    private final String targetName;

    TargetType(String targetName) {
        this.targetName = targetName;
    }

}
