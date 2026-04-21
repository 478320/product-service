package com.example.productservice.mapper;

import com.example.productservice.domain.entity.BannedWord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BannedWordMapper {

    @Select("""
            SELECT id, word, enabled, is_deleted, created_at, updated_at
            FROM banned_word
            WHERE enabled = 1 AND is_deleted = 0
            """)
    List<BannedWord> selectEnabled();

    @Insert("""
            INSERT INTO banned_word(word, enabled, is_deleted)
            VALUES(#{word}, #{enabled}, 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BannedWord bannedWord);
}
