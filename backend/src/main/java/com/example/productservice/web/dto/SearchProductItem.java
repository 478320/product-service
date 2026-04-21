package com.example.productservice.web.dto;

import lombok.Data;

@Data
public class SearchProductItem {
    private Long spuId;
    private Long skuId;
    private String spuTitle;
    private String skuName;
    private String brandName;
    private String categoryName;
    private Integer sales;
    private String highlight;
}
