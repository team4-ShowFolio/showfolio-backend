package com.example.showfolio.controller;

import com.example.showfolio.dto.ReportProcessRequest;
import com.example.showfolio.dto.ReportProcessResponse;
import com.example.showfolio.dto.ReportProcessSearchCondition;
import com.example.showfolio.dto.ReportResponse;
import com.example.showfolio.dto.ReportSearchCondition;
import com.example.showfolio.port.ReportProcessor;
import com.example.showfolio.port.ReportReader;
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
@RequestMapping("/api/admin/reports")
public class AdminReportController {

    private final ReportReader reportReader;
    private final ReportProcessor reportProcessor;

    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getReports(
            @ModelAttribute ReportSearchCondition condition,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reportReader.getAll(condition, pageable));
    }

    @GetMapping("/{id}/histories")
    public ResponseEntity<ReportProcessResponse> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportReader.getProcessByReportId(id));
    }

    @GetMapping("/histories")
    public ResponseEntity<Page<ReportProcessResponse>> getProcesses(
            @ModelAttribute ReportProcessSearchCondition condition,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reportReader.getAllProcesses(condition, pageable));
    }

    @PostMapping("/process")
    public ResponseEntity<Void> process(@Valid @RequestBody ReportProcessRequest request) {
        reportProcessor.process(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/process")
    public ResponseEntity<Void> update(@Valid @RequestBody ReportProcessRequest request) {
        reportProcessor.update(request);
        return ResponseEntity.noContent().build();
    }
}
