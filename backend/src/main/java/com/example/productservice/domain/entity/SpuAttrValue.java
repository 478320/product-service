package com.example.productservice.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SpuAttrValue {
    private Long id;
    private Long spuId;
    private String attrName;
    private String attrValue;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
