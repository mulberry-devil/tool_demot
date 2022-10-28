package com.caston.quartz.service.impl;

import com.caston.quartz.entity.TaskLog;
import com.caston.quartz.mapper.TaskLogMapper;
import com.caston.quartz.service.TaskLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定时任务日志表 服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-10-27
 */
@Service
public class TaskLogServiceImpl extends ServiceImpl<TaskLogMapper, TaskLog> implements TaskLogService {

}
