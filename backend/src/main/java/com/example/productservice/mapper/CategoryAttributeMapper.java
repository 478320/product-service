package com.example.productservice.mapper;

import com.example.productservice.domain.entity.CategoryAttribute;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryAttributeMapper {

    @Insert("""
            INSERT INTO category_attribute(category_id, attr_name, attr_scope, required_flag, data_type, is_deleted)
            VALUES(#{categoryId}, #{attrName}, #{attrScope}, #{requiredFlag}, #{dataType}, 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CategoryAttribute attribute);

    @Select("""
            SELECT id, category_id, attr_name, attr_scope, required_flag, data_type, is_deleted, created_at, updated_at
            FROM category_attribute
            WHERE category_id = #{categoryId} AND is_deleted = 0
            ORDER BY required_flag DESC, id ASC
            """)
    List<CategoryAttribute> selectByCategoryId(Long categoryId);

    @Select("""
            SELECT id, category_id, attr_name, attr_scope, required_flag, data_type, is_deleted, created_at, updated_at
            FROM category_attribute
            WHERE category_id = #{categoryId}
              AND attr_scope = #{attrScope}
              AND required_flag = 1
              AND is_deleted = 0
            """)
    List<CategoryAttribute> selectRequiredByCategoryAndScope(@Param("categoryId") Long categoryId, @Param("attrScope") String attrScope);
}
