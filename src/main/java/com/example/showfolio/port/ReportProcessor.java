package com.example.showfolio.port;

import com.example.showfolio.entity.ProcessStatus;

/**
 * 신고 처리를 위한 기능을 구현하기 위한 인터페이스 입니다.
 *
 */
public interface ReportProcessor {

    void process(Long reportId, Long adminId, ProcessStatus status, String reason);
}
