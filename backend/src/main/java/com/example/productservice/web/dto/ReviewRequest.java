package com.example.productservice.web.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private String reviewer;
    private String comment;
}
