package com.example.productservice.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateSkuRequest {
    @NotNull(message = "spuId 不能为空")
    private Long spuId;
    @NotBlank(message = "skuCode 不能为空")
    private String skuCode;
    @NotBlank(message = "skuName 不能为空")
    private String skuName;
    @NotNull(message = "price 不能为空")
    private BigDecimal price;
    @NotNull(message = "stock 不能为空")
    private Integer stock;
    private Integer sales = 0;
    @Valid
    private List<AttrValueRequest> skuAttributes;
}
