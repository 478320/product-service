package com.example.productservice.mapper;

import com.example.productservice.domain.entity.Sku;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SkuMapper {

    @Insert("""
            INSERT INTO sku(spu_id, sku_code, sku_name, price, stock, sales, sku_status, is_deleted)
            VALUES(#{spuId}, #{skuCode}, #{skuName}, #{price}, #{stock}, #{sales}, #{skuStatus}, 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Sku sku);

    @Select("""
            SELECT id, spu_id, sku_code, sku_name, price, stock, sales, sku_status, is_deleted, created_at, updated_at
            FROM sku
            WHERE spu_id = #{spuId} AND is_deleted = 0
            ORDER BY id ASC
            """)
    List<Sku> selectBySpuId(Long spuId);

    @Select("""
            SELECT id, spu_id, sku_code, sku_name, price, stock, sales, sku_status, is_deleted, created_at, updated_at
            FROM sku
            WHERE id = #{id} AND is_deleted = 0
            """)
    Sku selectById(Long id);

    @Select("""
            SELECT id, spu_id, sku_code, sku_name, price, stock, sales, sku_status, is_deleted, created_at, updated_at
            FROM sku
            WHERE sku_code = #{skuCode} AND is_deleted = 0
            LIMIT 1
            """)
    Sku selectBySkuCode(String skuCode);

    @Update("""
            UPDATE sku SET sku_status = #{status}, updated_at = NOW()
            WHERE spu_id = #{spuId} AND is_deleted = 0
            """)
    int updateStatusBySpuId(@Param("spuId") Long spuId, @Param("status") String status);

    @Select("""
            SELECT id, spu_id, sku_code, sku_name, price, stock, sales, sku_status, is_deleted, created_at, updated_at
            FROM sku
            WHERE is_deleted = 0
            """)
    List<Sku> selectAllAlive();
}
