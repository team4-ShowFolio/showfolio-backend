package com.example.showfolio.entity;

import lombok.Getter;

/**
 * 현재는 ENUM 으로 관리하지만 항목이 많아질 경우 DB에서 관리하도록 수정 필요
 *
 */
@Getter
public enum ReportReason {

    SPAM("스팸"),
    ADVERTISEMENT("광고, 홍보"),
    ABUSIVE_LANGUAGE("욕설, 비방"),
    HARASSMENT("괴롭힘, 혐오 표현"),
    FALSE_INFORMATION("허위 정보"),
    FRAUD("사기, 기만 행위"),
    VIOLENCE("폭력적 콘텐츠"),
    ILLEGAL_CONTENT("불법 콘텐츠"),
    COPYRIGHT_INFRINGEMENT("저작권 침해"),
    OFF_TOPIC("주제와 무관한 내용"),
    OTHER("기타");

    private final String reasonName;

    ReportReason(String reasonName) {
        this.reasonName = reasonName;
    }

}
