package com.example.showfolio.port;

import com.example.showfolio.dto.ReportCreateRequest;

/**
 * 피드, 댓글 등에 대해서 신고를 위한 기능을 제공하는 인터페이스 입니다.
 *
 */
public interface Reporter {

    void report(ReportCreateRequest request);

}
