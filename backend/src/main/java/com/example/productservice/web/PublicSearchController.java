package com.example.productservice.web;

import com.example.productservice.common.ApiResponse;
import com.example.productservice.service.SearchService;
import com.example.productservice.web.dto.PageResult;
import com.example.productservice.web.dto.SearchProductItem;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class PublicSearchController {

    private final SearchService searchService;

    @GetMapping("/products")
    public ApiResponse<PageResult<SearchProductItem>> search(@RequestParam(value = "q", required = false) String q,
                                                             @RequestParam(value = "brand", required = false) String brand,
                                                             @RequestParam(value = "category", required = false) String category,
                                                             @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                                             @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return ApiResponse.ok(searchService.search(q, brand, category, pageNo, pageSize));
    }
}
