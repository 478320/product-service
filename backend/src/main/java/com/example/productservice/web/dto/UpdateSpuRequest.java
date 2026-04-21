package com.example.productservice.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateSpuRequest {
    @NotBlank(message = "SPU 标题不能为空")
    private String title;
    @NotNull(message = "brandId 不能为空")
    private Long brandId;
    @NotNull(message = "categoryId 不能为空")
    private Long categoryId;
    private String description;
    @Valid
    private List<AttrValueRequest> spuAttributes;
}
