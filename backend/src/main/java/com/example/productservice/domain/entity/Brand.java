package com.example.productservice.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Brand {
    private Long id;
    private String name;
    private Integer priority;
    private String description;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
