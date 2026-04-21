package com.example.productservice.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmitReviewRequest {
    @NotNull(message = "spuId 不能为空")
    private Long spuId;
    @NotBlank(message = "发布策略不能为空")
    private String strategy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledPublishTime;
    private String operator;
}
