package com.example.showfolio.controller;

import com.example.showfolio.dto.CreateReportRequest;
import com.example.showfolio.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> createReport(
            @Valid @RequestBody CreateReportRequest request
    ) {
        reportService.report(request);
        return ResponseEntity.noContent().build();
    }
}
