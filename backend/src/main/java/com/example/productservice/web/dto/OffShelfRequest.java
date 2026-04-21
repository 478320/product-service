package com.example.productservice.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OffShelfRequest {
    @NotNull(message = "spuId 不能为空")
    private Long spuId;
    private String operator;
}
