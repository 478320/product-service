package com.example.productservice.mapper;

import com.example.productservice.domain.entity.Spu;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SpuMapper {

    @Insert("""
            INSERT INTO spu(title, brand_id, category_id, description, publish_status, publish_strategy, scheduled_publish_time, reject_reason, is_deleted)
            VALUES(#{title}, #{brandId}, #{categoryId}, #{description}, #{publishStatus}, #{publishStrategy}, #{scheduledPublishTime}, #{rejectReason}, 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Spu spu);

    @Update("""
            UPDATE spu
            SET title = #{title},
                brand_id = #{brandId},
                category_id = #{categoryId},
                description = #{description},
                updated_at = NOW()
            WHERE id = #{id} AND is_deleted = 0
            """)
    int updateBase(Spu spu);

    @Update("""
            UPDATE spu
            SET publish_status = #{status},
                publish_strategy = #{strategy},
                scheduled_publish_time = #{scheduledTime},
                reject_reason = #{rejectReason},
                updated_at = NOW()
            WHERE id = #{spuId} AND is_deleted = 0
            """)
    int updatePublishInfo(@Param("spuId") Long spuId,
                          @Param("status") String status,
                          @Param("strategy") String strategy,
                          @Param("scheduledTime") LocalDateTime scheduledTime,
                          @Param("rejectReason") String rejectReason);

    @Select("""
            SELECT id, title, brand_id, category_id, description, publish_status, publish_strategy, scheduled_publish_time, reject_reason, is_deleted, created_at, updated_at
            FROM spu
            WHERE id = #{id} AND is_deleted = 0
            """)
    Spu selectById(Long id);

    @Select("""
            SELECT id, title, brand_id, category_id, description, publish_status, publish_strategy, scheduled_publish_time, reject_reason, is_deleted, created_at, updated_at
            FROM spu
            WHERE is_deleted = 0
            ORDER BY id DESC
            LIMIT #{limit}
            """)
    List<Spu> selectLatest(@Param("limit") Integer limit);

    @Select("""
            SELECT id, title, brand_id, category_id, description, publish_status, publish_strategy, scheduled_publish_time, reject_reason, is_deleted, created_at, updated_at
            FROM spu
            WHERE is_deleted = 0
            """)
    List<Spu> selectAllAlive();

    @Select("""
            SELECT id, title, brand_id, category_id, description, publish_status, publish_strategy, scheduled_publish_time, reject_reason, is_deleted, created_at, updated_at
            FROM spu
            WHERE is_deleted = 0 AND publish_status = #{status}
            """)
    List<Spu> selectByStatus(String status);
}
