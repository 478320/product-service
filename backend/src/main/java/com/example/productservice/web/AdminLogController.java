package com.example.productservice.web;

import com.example.productservice.common.ApiResponse;
import com.example.productservice.common.RoleGuard;
import com.example.productservice.domain.entity.OperationLog;
import com.example.productservice.domain.enums.UserRole;
import com.example.productservice.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/logs")
public class AdminLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    public ApiResponse<List<OperationLog>> latest(@RequestHeader("X-Role") String role,
                                                  @RequestParam(value = "limit", required = false) Integer limit) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(operationLogService.latest(limit));
    }
}
