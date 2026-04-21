package com.example.productservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttrValueRequest {
    @NotBlank(message = "属性名不能为空")
    private String attrName;
    @NotBlank(message = "属性值不能为空")
    private String attrValue;
}
