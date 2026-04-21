package com.example.productservice.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SkuAttrValue {
    private Long id;
    private Long skuId;
    private String attrName;
    private String attrValue;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
