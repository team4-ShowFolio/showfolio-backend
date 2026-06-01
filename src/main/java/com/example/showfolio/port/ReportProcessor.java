package com.example.showfolio.port;

import com.example.showfolio.dto.ReportProcessRequest;

/**
 * 신고 처리를 위한 기능을 구현하기 위한 인터페이스 입니다.
 *
 */
public interface ReportProcessor {

    void process(Long reportId, ReportProcessRequest request);

    void update(Long reportId, ReportProcessRequest request);
}
