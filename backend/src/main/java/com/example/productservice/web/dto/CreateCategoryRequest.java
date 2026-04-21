package com.example.productservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotBlank(message = "类目名称不能为空")
    private String name;
    private Long parentId = 0L;
}
