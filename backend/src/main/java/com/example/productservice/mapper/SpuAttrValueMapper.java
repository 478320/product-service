package com.example.productservice.mapper;

import com.example.productservice.domain.entity.SpuAttrValue;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SpuAttrValueMapper {

    @Delete("""
            DELETE FROM spu_attr_value WHERE spu_id = #{spuId}
            """)
    int deleteBySpuId(Long spuId);

    @Insert({
            "<script>",
            "INSERT INTO spu_attr_value(spu_id, attr_name, attr_value, is_deleted) VALUES ",
            "<foreach collection='values' item='item' separator=','>",
            "(#{item.spuId}, #{item.attrName}, #{item.attrValue}, 0)",
            "</foreach>",
            "</script>"
    })
    int batchInsert(@Param("values") List<SpuAttrValue> values);

    @Select("""
            SELECT id, spu_id, attr_name, attr_value, is_deleted, created_at, updated_at
            FROM spu_attr_value
            WHERE spu_id = #{spuId} AND is_deleted = 0
            """)
    List<SpuAttrValue> selectBySpuId(Long spuId);
}
