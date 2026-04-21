package com.example.productservice.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewRecord {
    private Long id;
    private Long publishTaskId;
    private Long spuId;
    private String decision;
    private String comment;
    private String reviewer;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
