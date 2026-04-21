package com.example.productservice.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Sku {
    private Long id;
    private Long spuId;
    private String skuCode;
    private String skuName;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private String skuStatus;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
