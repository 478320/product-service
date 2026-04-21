package com.example.productservice.service;

import com.example.productservice.common.BizException;
import com.example.productservice.domain.entity.Brand;
import com.example.productservice.domain.entity.Category;
import com.example.productservice.domain.entity.CategoryAttribute;
import com.example.productservice.domain.entity.Sku;
import com.example.productservice.domain.entity.SkuAttrValue;
import com.example.productservice.domain.entity.Spu;
import com.example.productservice.domain.entity.SpuAttrValue;
import com.example.productservice.domain.enums.PublishStatus;
import com.example.productservice.mapper.BrandMapper;
import com.example.productservice.mapper.CategoryAttributeMapper;
import com.example.productservice.mapper.CategoryMapper;
import com.example.productservice.mapper.SkuAttrValueMapper;
import com.example.productservice.mapper.SkuMapper;
import com.example.productservice.mapper.SpuAttrValueMapper;
import com.example.productservice.mapper.SpuMapper;
import com.example.productservice.web.dto.AttrValueRequest;
import com.example.productservice.web.dto.CreateBrandRequest;
import com.example.productservice.web.dto.CreateCategoryAttributeRequest;
import com.example.productservice.web.dto.CreateCategoryRequest;
import com.example.productservice.web.dto.CreateSkuRequest;
import com.example.productservice.web.dto.CreateSpuRequest;
import com.example.productservice.web.dto.UpdateSpuRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductModelingService {

    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryAttributeMapper categoryAttributeMapper;
    private final SpuMapper spuMapper;
    private final SkuMapper skuMapper;
    private final SpuAttrValueMapper spuAttrValueMapper;
    private final SkuAttrValueMapper skuAttrValueMapper;
    private final OperationLogService operationLogService;

    public Brand createBrand(CreateBrandRequest request, String operator) {
        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setPriority(request.getPriority() == null ? 1 : request.getPriority());
        brand.setDescription(request.getDescription());
        brandMapper.insert(brand);
        operationLogService.record("BRAND", brand.getId(), "CREATE", operator, "创建品牌: " + brand.getName());
        return brand;
    }

    public Category createCategory(CreateCategoryRequest request, String operator) {
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        categoryMapper.insert(category);
        operationLogService.record("CATEGORY", category.getId(), "CREATE", operator, "创建类目: " + category.getName());
        return category;
    }

    public CategoryAttribute createCategoryAttribute(CreateCategoryAttributeRequest request, String operator) {
        if (categoryMapper.selectById(request.getCategoryId()) == null) {
            throw new BizException("类目不存在: " + request.getCategoryId());
        }
        String scope = request.getAttrScope().trim().toUpperCase();
        if (!"SPU".equals(scope) && !"SKU".equals(scope)) {
            throw new BizException("attrScope 仅支持 SPU 或 SKU");
        }
        CategoryAttribute attribute = new CategoryAttribute();
        attribute.setCategoryId(request.getCategoryId());
        attribute.setAttrName(request.getAttrName());
        attribute.setAttrScope(scope);
        attribute.setRequiredFlag(request.getRequiredFlag());
        attribute.setDataType(request.getDataType());
        categoryAttributeMapper.insert(attribute);
        operationLogService.record("CATEGORY_ATTR", attribute.getId(), "CREATE", operator,
                "创建类目属性: " + attribute.getAttrName() + ", scope=" + scope);
        return attribute;
    }

    public List<Brand> listBrands() {
        return brandMapper.selectAll();
    }

    public List<Category> listCategories() {
        return categoryMapper.selectAll();
    }

    public List<CategoryAttribute> listCategoryAttributes(Long categoryId) {
        return categoryAttributeMapper.selectByCategoryId(categoryId);
    }

    @Transactional
    public Spu createSpu(CreateSpuRequest request, String operator) {
        ensureBrandAndCategoryExist(request.getBrandId(), request.getCategoryId());
        Spu spu = new Spu();
        spu.setTitle(request.getTitle());
        spu.setBrandId(request.getBrandId());
        spu.setCategoryId(request.getCategoryId());
        spu.setDescription(request.getDescription());
        spu.setPublishStatus(PublishStatus.DRAFT.name());
        spu.setPublishStrategy("MANUAL_AFTER_REVIEW");
        spuMapper.insert(spu);
        replaceSpuAttributes(spu.getId(), request.getSpuAttributes());
        operationLogService.record("SPU", spu.getId(), "CREATE", operator, "创建SPU: " + spu.getTitle());
        return spuMapper.selectById(spu.getId());
    }

    @Transactional
    public Spu updateSpu(Long spuId, UpdateSpuRequest request, String operator) {
        Spu old = getSpuOrThrow(spuId);
        if (PublishStatus.PUBLISHED.name().equals(old.getPublishStatus())) {
            throw new BizException("已发布 SPU 不允许直接编辑，请先下架");
        }
        ensureBrandAndCategoryExist(request.getBrandId(), request.getCategoryId());
        old.setTitle(request.getTitle());
        old.setBrandId(request.getBrandId());
        old.setCategoryId(request.getCategoryId());
        old.setDescription(request.getDescription());
        spuMapper.updateBase(old);
        replaceSpuAttributes(spuId, request.getSpuAttributes());
        operationLogService.record("SPU", spuId, "UPDATE", operator, "更新SPU基础信息");
        return spuMapper.selectById(spuId);
    }

    @Transactional
    public Sku createSku(CreateSkuRequest request, String operator) {
        Spu spu = getSpuOrThrow(request.getSpuId());
        if (skuMapper.selectBySkuCode(request.getSkuCode()) != null) {
            throw new BizException("skuCode 已存在: " + request.getSkuCode());
        }
        Sku sku = new Sku();
        sku.setSpuId(request.getSpuId());
        sku.setSkuCode(request.getSkuCode());
        sku.setSkuName(request.getSkuName());
        sku.setPrice(request.getPrice());
        sku.setStock(request.getStock());
        sku.setSales(request.getSales() == null ? 0 : request.getSales());
        sku.setSkuStatus("DRAFT");
        skuMapper.insert(sku);
        replaceSkuAttributes(sku.getId(), request.getSkuAttributes());
        operationLogService.record("SKU", sku.getId(), "CREATE", operator, "创建SKU, spuId=" + spu.getId());
        return skuMapper.selectById(sku.getId());
    }

    public List<Spu> listLatestSpu(Integer limit) {
        int actual = limit == null ? 50 : Math.max(1, Math.min(limit, 500));
        return spuMapper.selectLatest(actual);
    }

    public List<Sku> listSkuBySpuId(Long spuId) {
        return skuMapper.selectBySpuId(spuId);
    }

    public Spu getSpuOrThrow(Long spuId) {
        Spu spu = spuMapper.selectById(spuId);
        if (spu == null) {
            throw new BizException("SPU 不存在: " + spuId);
        }
        return spu;
    }

    public void validateRequiredAttributesForPublish(Long spuId) {
        Spu spu = getSpuOrThrow(spuId);

        List<CategoryAttribute> requiredSpuAttrs =
                categoryAttributeMapper.selectRequiredByCategoryAndScope(spu.getCategoryId(), "SPU");
        Set<String> spuExistingAttrNames = new HashSet<>();
        for (SpuAttrValue value : spuAttrValueMapper.selectBySpuId(spuId)) {
            spuExistingAttrNames.add(value.getAttrName());
        }
        for (CategoryAttribute attr : requiredSpuAttrs) {
            if (!spuExistingAttrNames.contains(attr.getAttrName())) {
                throw new BizException("SPU 缺少必填属性: " + attr.getAttrName());
            }
        }

        List<Sku> skuList = skuMapper.selectBySpuId(spuId);
        if (skuList.isEmpty()) {
            throw new BizException("提交审核前至少创建一个 SKU");
        }

        List<CategoryAttribute> requiredSkuAttrs =
                categoryAttributeMapper.selectRequiredByCategoryAndScope(spu.getCategoryId(), "SKU");
        for (Sku sku : skuList) {
            Set<String> skuAttrNameSet = new HashSet<>();
            for (SkuAttrValue value : skuAttrValueMapper.selectBySkuId(sku.getId())) {
                skuAttrNameSet.add(value.getAttrName());
            }
            for (CategoryAttribute requiredSkuAttr : requiredSkuAttrs) {
                if (!skuAttrNameSet.contains(requiredSkuAttr.getAttrName())) {
                    throw new BizException("SKU[" + sku.getSkuCode() + "] 缺少必填属性: " + requiredSkuAttr.getAttrName());
                }
            }
        }
    }

    public String buildSpuSearchableText(Long spuId) {
        Spu spu = getSpuOrThrow(spuId);
        StringBuilder sb = new StringBuilder();
        sb.append(spu.getTitle()).append(" ");
        if (spu.getDescription() != null) {
            sb.append(spu.getDescription()).append(" ");
        }
        for (SpuAttrValue value : spuAttrValueMapper.selectBySpuId(spuId)) {
            sb.append(value.getAttrName()).append(" ").append(value.getAttrValue()).append(" ");
        }
        for (Sku sku : skuMapper.selectBySpuId(spuId)) {
            sb.append(sku.getSkuName()).append(" ");
            for (SkuAttrValue value : skuAttrValueMapper.selectBySkuId(sku.getId())) {
                sb.append(value.getAttrName()).append(" ").append(value.getAttrValue()).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private void ensureBrandAndCategoryExist(Long brandId, Long categoryId) {
        if (brandMapper.selectById(brandId) == null) {
            throw new BizException("品牌不存在: " + brandId);
        }
        if (categoryMapper.selectById(categoryId) == null) {
            throw new BizException("类目不存在: " + categoryId);
        }
    }

    private void replaceSpuAttributes(Long spuId, List<AttrValueRequest> attrs) {
        spuAttrValueMapper.deleteBySpuId(spuId);
        if (attrs == null || attrs.isEmpty()) {
            return;
        }
        List<SpuAttrValue> values = new ArrayList<>();
        for (AttrValueRequest attr : attrs) {
            SpuAttrValue value = new SpuAttrValue();
            value.setSpuId(spuId);
            value.setAttrName(attr.getAttrName());
            value.setAttrValue(attr.getAttrValue());
            values.add(value);
        }
        spuAttrValueMapper.batchInsert(values);
    }

    private void replaceSkuAttributes(Long skuId, List<AttrValueRequest> attrs) {
        skuAttrValueMapper.deleteBySkuId(skuId);
        if (attrs == null || attrs.isEmpty()) {
            return;
        }
        List<SkuAttrValue> values = new ArrayList<>();
        for (AttrValueRequest attr : attrs) {
            SkuAttrValue value = new SkuAttrValue();
            value.setSkuId(skuId);
            value.setAttrName(attr.getAttrName());
            value.setAttrValue(attr.getAttrValue());
            values.add(value);
        }
        skuAttrValueMapper.batchInsert(values);
    }
}
