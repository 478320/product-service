package com.example.productservice.mapper;

import com.example.productservice.domain.entity.PublishTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PublishTaskMapper {

    @Insert("""
            INSERT INTO publish_task(spu_id, strategy, scheduled_time, task_status, fail_reason, created_by, is_deleted)
            VALUES(#{spuId}, #{strategy}, #{scheduledTime}, #{taskStatus}, #{failReason}, #{createdBy}, 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PublishTask task);

    @Select("""
            SELECT id, spu_id, strategy, scheduled_time, task_status, fail_reason, created_by, is_deleted, created_at, updated_at
            FROM publish_task
            WHERE id = #{id} AND is_deleted = 0
            """)
    PublishTask selectById(Long id);

    @Select("""
            SELECT id, spu_id, strategy, scheduled_time, task_status, fail_reason, created_by, is_deleted, created_at, updated_at
            FROM publish_task
            WHERE spu_id = #{spuId} AND is_deleted = 0
            ORDER BY id DESC
            LIMIT 1
            """)
    PublishTask selectLatestBySpuId(Long spuId);

    @Update("""
            UPDATE publish_task
            SET task_status = #{taskStatus},
                fail_reason = #{failReason},
                updated_at = NOW()
            WHERE id = #{taskId} AND is_deleted = 0
            """)
    int updateStatus(@Param("taskId") Long taskId,
                     @Param("taskStatus") String taskStatus,
                     @Param("failReason") String failReason);

    @Select("""
            SELECT id, spu_id, strategy, scheduled_time, task_status, fail_reason, created_by, is_deleted, created_at, updated_at
            FROM publish_task
            WHERE task_status = 'WAITING_PUBLISH'
              AND strategy = 'SCHEDULED'
              AND scheduled_time IS NOT NULL
              AND scheduled_time <= #{time}
              AND is_deleted = 0
            ORDER BY scheduled_time ASC
            """)
    List<PublishTask> selectDueScheduledTasks(LocalDateTime time);

    @Select("""
            SELECT id, spu_id, strategy, scheduled_time, task_status, fail_reason, created_by, is_deleted, created_at, updated_at
            FROM publish_task
            WHERE is_deleted = 0
            ORDER BY id DESC
            LIMIT #{limit}
            """)
    List<PublishTask> selectLatest(@Param("limit") Integer limit);
}
