package com.example.showfolio.controller;

import com.example.showfolio.dto.AdminMemberResponse;
import com.example.showfolio.dto.MemberSearchCondition;
import com.example.showfolio.dto.MemberSuspendRequest;
import com.example.showfolio.service.AdminMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public ResponseEntity<Page<AdminMemberResponse>> getAll(
            MemberSearchCondition condition,
            Pageable pageable
    ) {
        return ResponseEntity.ok(adminMemberService.getAll(condition, pageable));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(
            @PathVariable Long id
    ) {
        adminMemberService.updateRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid MemberSuspendRequest request
    ) {
        adminMemberService.updateStatus(id, request);
        return ResponseEntity.noContent().build();
    }
}
