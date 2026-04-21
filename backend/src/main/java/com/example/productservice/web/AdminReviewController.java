package com.example.productservice.web;

import com.example.productservice.common.ApiResponse;
import com.example.productservice.common.RoleGuard;
import com.example.productservice.domain.entity.PublishTask;
import com.example.productservice.domain.enums.UserRole;
import com.example.productservice.service.ReviewService;
import com.example.productservice.web.dto.ReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/review")
public class AdminReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{taskId}/approve")
    public ApiResponse<PublishTask> approve(@RequestHeader("X-Role") String role,
                                            @PathVariable("taskId") Long taskId,
                                            @RequestBody ReviewRequest request) {
        RoleGuard.requireRole(role, UserRole.REVIEWER);
        return ApiResponse.ok(reviewService.approve(taskId, request));
    }

    @PostMapping("/{taskId}/reject")
    public ApiResponse<PublishTask> reject(@RequestHeader("X-Role") String role,
                                           @PathVariable("taskId") Long taskId,
                                           @RequestBody ReviewRequest request) {
        RoleGuard.requireRole(role, UserRole.REVIEWER);
        return ApiResponse.ok(reviewService.reject(taskId, request));
    }
}
