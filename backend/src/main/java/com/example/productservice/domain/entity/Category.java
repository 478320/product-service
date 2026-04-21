package com.example.productservice.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String name;
    private Long parentId;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
