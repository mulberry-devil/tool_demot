package com.caston.quartz.mapper;

import com.caston.quartz.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定时任务信息表 Mapper 接口
 * </p>
 *
 * @author caston
 * @since 2022-10-27
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

}
