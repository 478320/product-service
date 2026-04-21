package com.example.productservice.web;

import com.example.productservice.common.ApiResponse;
import com.example.productservice.common.RoleGuard;
import com.example.productservice.domain.entity.PublishTask;
import com.example.productservice.domain.enums.UserRole;
import com.example.productservice.service.PublishEngineService;
import com.example.productservice.web.dto.ExecutePublishRequest;
import com.example.productservice.web.dto.OffShelfRequest;
import com.example.productservice.web.dto.SubmitReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/publish")
public class AdminPublishController {

    private final PublishEngineService publishEngineService;

    @PostMapping("/submit-review")
    public ApiResponse<PublishTask> submitReview(@RequestHeader("X-Role") String role,
                                                 @Valid @RequestBody SubmitReviewRequest request) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(publishEngineService.submitReview(request));
    }

    @PostMapping("/execute")
    public ApiResponse<PublishTask> executePublish(@RequestHeader("X-Role") String role,
                                                   @Valid @RequestBody ExecutePublishRequest request) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(publishEngineService.executeManualPublish(request));
    }

    @PostMapping("/off-shelf")
    public ApiResponse<Void> offShelf(@RequestHeader("X-Role") String role,
                                      @Valid @RequestBody OffShelfRequest request) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        publishEngineService.offShelf(request);
        return ApiResponse.ok(null);
    }

    @GetMapping("/tasks")
    public ApiResponse<List<PublishTask>> listTasks(@RequestHeader("X-Role") String role,
                                                    @RequestParam(value = "limit", required = false) Integer limit) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(publishEngineService.listLatestTasks(limit));
    }
}
