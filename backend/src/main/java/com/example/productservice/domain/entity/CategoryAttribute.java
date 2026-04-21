package com.example.productservice.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryAttribute {
    private Long id;
    private Long categoryId;
    private String attrName;
    private String attrScope;
    private Integer requiredFlag;
    private String dataType;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
