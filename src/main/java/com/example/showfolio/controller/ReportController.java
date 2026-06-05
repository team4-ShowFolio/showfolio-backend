package com.example.showfolio.controller;

import com.example.showfolio.dto.ReportCreateRequest;
import com.example.showfolio.service.ReportCreateService;
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

    private final ReportCreateService reportCreateService;

    @PostMapping
    public ResponseEntity<Void> createReport(
            @Valid @RequestBody ReportCreateRequest request
    ) {
        reportCreateService.report(request);
        return ResponseEntity.noContent().build();
    }
}
