package com.example.productservice.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublishTask {
    private Long id;
    private Long spuId;
    private String strategy;
    private LocalDateTime scheduledTime;
    private String taskStatus;
    private String failReason;
    private String createdBy;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
