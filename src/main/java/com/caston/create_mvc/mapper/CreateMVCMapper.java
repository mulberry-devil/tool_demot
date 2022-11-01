package com.caston.create_mvc.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CreateMVCMapper {
    @Delete("${sqlStr}")
    void excuteSQL(@Param("sqlStr") String sql);
}
