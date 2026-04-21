package com.example.productservice.mapper;

import com.example.productservice.domain.entity.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BrandMapper {

    @Insert("""
            INSERT INTO brand(name, priority, description, is_deleted)
            VALUES(#{name}, #{priority}, #{description}, 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Brand brand);

    @Select("""
            SELECT id, name, priority, description, is_deleted, created_at, updated_at
            FROM brand
            WHERE is_deleted = 0
            ORDER BY priority DESC, id ASC
            """)
    List<Brand> selectAll();

    @Select("""
            SELECT id, name, priority, description, is_deleted, created_at, updated_at
            FROM brand
            WHERE id = #{id} AND is_deleted = 0
            """)
    Brand selectById(Long id);
}
