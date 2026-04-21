package com.example.productservice.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Spu {
    private Long id;
    private String title;
    private Long brandId;
    private Long categoryId;
    private String description;
    private String publishStatus;
    private String publishStrategy;
    private LocalDateTime scheduledPublishTime;
    private String rejectReason;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
