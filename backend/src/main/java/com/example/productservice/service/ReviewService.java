package com.example.productservice.service;

import com.example.productservice.domain.entity.PublishTask;
import com.example.productservice.web.dto.ReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final PublishEngineService publishEngineService;

    public PublishTask approve(Long taskId, ReviewRequest request) {
        return publishEngineService.approve(taskId, request);
    }

    public PublishTask reject(Long taskId, ReviewRequest request) {
        return publishEngineService.reject(taskId, request);
    }
}
