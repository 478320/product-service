package com.example.productservice.mapper;

import com.example.productservice.domain.entity.SkuAttrValue;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SkuAttrValueMapper {

    @Delete("""
            DELETE FROM sku_attr_value WHERE sku_id = #{skuId}
            """)
    int deleteBySkuId(Long skuId);

    @Insert({
            "<script>",
            "INSERT INTO sku_attr_value(sku_id, attr_name, attr_value, is_deleted) VALUES ",
            "<foreach collection='values' item='item' separator=','>",
            "(#{item.skuId}, #{item.attrName}, #{item.attrValue}, 0)",
            "</foreach>",
            "</script>"
    })
    int batchInsert(@Param("values") List<SkuAttrValue> values);

    @Select("""
            SELECT id, sku_id, attr_name, attr_value, is_deleted, created_at, updated_at
            FROM sku_attr_value
            WHERE sku_id = #{skuId} AND is_deleted = 0
            """)
    List<SkuAttrValue> selectBySkuId(Long skuId);
}
