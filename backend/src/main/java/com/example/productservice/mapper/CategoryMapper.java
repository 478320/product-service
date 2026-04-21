package com.example.productservice.mapper;

import com.example.productservice.domain.entity.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert("""
            INSERT INTO category(name, parent_id, is_deleted)
            VALUES(#{name}, #{parentId}, 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Select("""
            SELECT id, name, parent_id, is_deleted, created_at, updated_at
            FROM category
            WHERE is_deleted = 0
            ORDER BY id ASC
            """)
    List<Category> selectAll();

    @Select("""
            SELECT id, name, parent_id, is_deleted, created_at, updated_at
            FROM category
            WHERE id = #{id} AND is_deleted = 0
            """)
    Category selectById(Long id);
}
