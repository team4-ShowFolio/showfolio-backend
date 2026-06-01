package com.example.showfolio.port;

import com.example.showfolio.entity.ProcessStatus;
import com.example.showfolio.entity.Report;

/**
 * 신고 처리(검토중/처리/반려) 에 따라서 달라지는 구현을 분리하여 관리하기 위한 인터페이스입니다.
 *
 */
public interface ReportProcessHandler {

    ProcessStatus supportedStatus();

    void handle(Report report, Long adminId, String reason);
}
