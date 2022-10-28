package com.caston.quartz.mapper;

import com.caston.quartz.entity.TaskLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定时任务日志表 Mapper 接口
 * </p>
 *
 * @author caston
 * @since 2022-10-27
 */
@Mapper
public interface TaskLogMapper extends BaseMapper<TaskLog> {

}
