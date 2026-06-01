package com.example.showfolio.controller;

import com.example.showfolio.dto.CreateReportRequest;
import com.example.showfolio.dto.ReportResponse;
import com.example.showfolio.dto.ReportSearchCondition;
import com.example.showfolio.port.ReportReader;
import com.example.showfolio.service.ReportCreateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportCreateService reportCreateService;
    private final ReportReader reportReader;

    @PostMapping
    public ResponseEntity<Void> createReport(
            @Valid @RequestBody CreateReportRequest request
    ) {
        reportCreateService.report(request);
        return ResponseEntity.noContent().build();
    }

    // TODO ADMIN 권한 설정 필요
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportReader.getById(id));
    }

    // TODO ADMIN 권한 설정 필요
    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getReports(
            @ModelAttribute ReportSearchCondition condition,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reportReader.getAll(condition, pageable));
    }
}
