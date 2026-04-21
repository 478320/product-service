package com.example.productservice.mapper;

import com.example.productservice.domain.entity.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Insert("""
            INSERT INTO operation_log(biz_type, biz_id, action, operator, detail, is_deleted)
            VALUES(#{bizType}, #{bizId}, #{action}, #{operator}, #{detail}, 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);

    @Select("""
            SELECT id, biz_type, biz_id, action, operator, detail, is_deleted, created_at
            FROM operation_log
            WHERE is_deleted = 0
            ORDER BY id DESC
            LIMIT #{limit}
            """)
    List<OperationLog> selectLatest(@Param("limit") Integer limit);
}
