package com.example.productservice.mapper;

import com.example.productservice.domain.entity.ReviewRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewRecordMapper {

    @Insert("""
            INSERT INTO review_record(publish_task_id, spu_id, decision, comment, reviewer, is_deleted)
            VALUES(#{publishTaskId}, #{spuId}, #{decision}, #{comment}, #{reviewer}, 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReviewRecord record);

    @Select("""
            SELECT id, publish_task_id, spu_id, decision, comment, reviewer, is_deleted, created_at, updated_at
            FROM review_record
            WHERE publish_task_id = #{taskId} AND is_deleted = 0
            ORDER BY id DESC
            """)
    List<ReviewRecord> selectByTaskId(Long taskId);
}
