package com.example.showfolio.controller;

import com.example.showfolio.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @PutMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(
            @PathVariable Long id
    ) {
        adminMemberService.updateRole();
        return ResponseEntity.noContent().build();
    }

}
