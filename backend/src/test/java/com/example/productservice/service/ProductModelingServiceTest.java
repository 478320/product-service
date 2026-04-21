package com.example.productservice.service;

import com.example.productservice.common.BizException;
import com.example.productservice.domain.entity.CategoryAttribute;
import com.example.productservice.domain.entity.Sku;
import com.example.productservice.domain.entity.Spu;
import com.example.productservice.mapper.BrandMapper;
import com.example.productservice.mapper.CategoryAttributeMapper;
import com.example.productservice.mapper.CategoryMapper;
import com.example.productservice.mapper.SkuAttrValueMapper;
import com.example.productservice.mapper.SkuMapper;
import com.example.productservice.mapper.SpuAttrValueMapper;
import com.example.productservice.mapper.SpuMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductModelingServiceTest {

    @Mock
    private BrandMapper brandMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryAttributeMapper categoryAttributeMapper;
    @Mock
    private SpuMapper spuMapper;
    @Mock
    private SkuMapper skuMapper;
    @Mock
    private SpuAttrValueMapper spuAttrValueMapper;
    @Mock
    private SkuAttrValueMapper skuAttrValueMapper;
    @Mock
    private OperationLogService operationLogService;

    @InjectMocks
    private ProductModelingService service;

    @Test
    void shouldThrowWhenMissingRequiredSpuAttribute() {
        Spu spu = new Spu();
        spu.setId(1L);
        spu.setCategoryId(100L);
        when(spuMapper.selectById(1L)).thenReturn(spu);

        CategoryAttribute requiredAttr = new CategoryAttribute();
        requiredAttr.setAttrName("材质");
        when(categoryAttributeMapper.selectRequiredByCategoryAndScope(100L, "SPU"))
                .thenReturn(List.of(requiredAttr));
        when(spuAttrValueMapper.selectBySpuId(1L)).thenReturn(List.of());

        Assertions.assertThrows(BizException.class, () -> service.validateRequiredAttributesForPublish(1L));
    }

    @Test
    void shouldThrowWhenNoSkuBeforeSubmitReview() {
        Spu spu = new Spu();
        spu.setId(1L);
        spu.setCategoryId(100L);
        when(spuMapper.selectById(1L)).thenReturn(spu);
        when(categoryAttributeMapper.selectRequiredByCategoryAndScope(100L, "SPU")).thenReturn(List.of());
        when(spuAttrValueMapper.selectBySpuId(1L)).thenReturn(List.of());
        when(skuMapper.selectBySpuId(1L)).thenReturn(List.of());

        Assertions.assertThrows(BizException.class, () -> service.validateRequiredAttributesForPublish(1L));
    }

    @Test
    void shouldThrowWhenSkuMissingRequiredSkuAttribute() {
        Spu spu = new Spu();
        spu.setId(1L);
        spu.setCategoryId(100L);
        when(spuMapper.selectById(1L)).thenReturn(spu);
        when(categoryAttributeMapper.selectRequiredByCategoryAndScope(100L, "SPU")).thenReturn(List.of());
        when(spuAttrValueMapper.selectBySpuId(1L)).thenReturn(List.of());

        Sku sku = new Sku();
        sku.setId(8L);
        sku.setSkuCode("SKU000008");
        when(skuMapper.selectBySpuId(1L)).thenReturn(List.of(sku));

        CategoryAttribute requiredSkuAttr = new CategoryAttribute();
        requiredSkuAttr.setAttrName("颜色");
        when(categoryAttributeMapper.selectRequiredByCategoryAndScope(100L, "SKU"))
                .thenReturn(List.of(requiredSkuAttr));
        when(skuAttrValueMapper.selectBySkuId(8L)).thenReturn(List.of());

        Assertions.assertThrows(BizException.class, () -> service.validateRequiredAttributesForPublish(1L));
    }
}
