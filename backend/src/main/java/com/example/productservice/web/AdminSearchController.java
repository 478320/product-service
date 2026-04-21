package com.example.productservice.web;

import com.example.productservice.common.ApiResponse;
import com.example.productservice.common.RoleGuard;
import com.example.productservice.domain.enums.UserRole;
import com.example.productservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/search")
public class AdminSearchController {

    private final SearchService searchService;

    @PostMapping("/reindex")
    public ApiResponse<Map<String, Object>> reindex(@RequestHeader("X-Role") String role) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        int count = searchService.reindexPublishedProducts();
        return ApiResponse.ok(Map.of("reindexedCount", count));
    }
}
