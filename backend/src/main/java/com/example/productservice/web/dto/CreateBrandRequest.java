package com.example.productservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBrandRequest {
    @NotBlank(message = "品牌名不能为空")
    private String name;
    private Integer priority = 1;
    private String description;
}
