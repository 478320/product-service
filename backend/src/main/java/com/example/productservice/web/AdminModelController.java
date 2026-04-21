package com.example.productservice.web;

import com.example.productservice.common.ApiResponse;
import com.example.productservice.common.RoleGuard;
import com.example.productservice.domain.entity.Brand;
import com.example.productservice.domain.entity.Category;
import com.example.productservice.domain.entity.CategoryAttribute;
import com.example.productservice.domain.entity.Sku;
import com.example.productservice.domain.entity.Spu;
import com.example.productservice.domain.enums.UserRole;
import com.example.productservice.service.ProductModelingService;
import com.example.productservice.web.dto.CreateBrandRequest;
import com.example.productservice.web.dto.CreateCategoryAttributeRequest;
import com.example.productservice.web.dto.CreateCategoryRequest;
import com.example.productservice.web.dto.CreateSkuRequest;
import com.example.productservice.web.dto.CreateSpuRequest;
import com.example.productservice.web.dto.UpdateSpuRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminModelController {

    private final ProductModelingService productModelingService;

    @PostMapping("/brand")
    public ApiResponse<Brand> createBrand(@RequestHeader("X-Role") String role,
                                          @Valid @RequestBody CreateBrandRequest request) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.createBrand(request, role));
    }

    @GetMapping("/brand")
    public ApiResponse<List<Brand>> listBrands(@RequestHeader("X-Role") String role) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.listBrands());
    }

    @PostMapping("/category")
    public ApiResponse<Category> createCategory(@RequestHeader("X-Role") String role,
                                                @Valid @RequestBody CreateCategoryRequest request) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.createCategory(request, role));
    }

    @GetMapping("/category")
    public ApiResponse<List<Category>> listCategories(@RequestHeader("X-Role") String role) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.listCategories());
    }

    @PostMapping("/category-attribute")
    public ApiResponse<CategoryAttribute> createCategoryAttribute(@RequestHeader("X-Role") String role,
                                                                  @Valid @RequestBody CreateCategoryAttributeRequest request) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.createCategoryAttribute(request, role));
    }

    @GetMapping("/category-attribute")
    public ApiResponse<List<CategoryAttribute>> listCategoryAttributes(@RequestHeader("X-Role") String role,
                                                                       @RequestParam("categoryId") Long categoryId) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.listCategoryAttributes(categoryId));
    }

    @PostMapping("/spu")
    public ApiResponse<Spu> createSpu(@RequestHeader("X-Role") String role,
                                      @Valid @RequestBody CreateSpuRequest request) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.createSpu(request, role));
    }

    @PutMapping("/spu/{id}")
    public ApiResponse<Spu> updateSpu(@RequestHeader("X-Role") String role,
                                      @PathVariable("id") Long id,
                                      @Valid @RequestBody UpdateSpuRequest request) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.updateSpu(id, request, role));
    }

    @GetMapping("/spu")
    public ApiResponse<List<Spu>> listLatestSpu(@RequestHeader("X-Role") String role,
                                                @RequestParam(value = "limit", required = false) Integer limit) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.listLatestSpu(limit));
    }

    @PostMapping("/sku")
    public ApiResponse<Sku> createSku(@RequestHeader("X-Role") String role,
                                      @Valid @RequestBody CreateSkuRequest request) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.createSku(request, role));
    }

    @GetMapping("/sku")
    public ApiResponse<List<Sku>> listSkuBySpu(@RequestHeader("X-Role") String role,
                                               @RequestParam("spuId") Long spuId) {
        RoleGuard.requireRole(role, UserRole.OPERATOR, UserRole.REVIEWER);
        return ApiResponse.ok(productModelingService.listSkuBySpuId(spuId));
    }
}
