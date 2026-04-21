package com.example.productservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCategoryAttributeRequest {
    @NotNull(message = "categoryId 不能为空")
    private Long categoryId;
    @NotBlank(message = "属性名不能为空")
    private String attrName;
    @NotBlank(message = "属性范围不能为空")
    private String attrScope;
    @NotNull(message = "是否必填不能为空")
    private Integer requiredFlag;
    @NotBlank(message = "数据类型不能为空")
    private String dataType;
}
