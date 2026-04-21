package com.example.productservice.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private String bizType;
    private Long bizId;
    private String action;
    private String operator;
    private String detail;
    private Integer isDeleted;
    private LocalDateTime createdAt;
}
