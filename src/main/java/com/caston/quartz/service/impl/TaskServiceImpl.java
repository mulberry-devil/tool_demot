package com.caston.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caston.quartz.entity.Task;
import com.caston.quartz.manager.QuartzManager;
import com.caston.quartz.mapper.TaskMapper;
import com.caston.quartz.service.TaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 定时任务信息表 服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-10-27
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    private final static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Resource
    private TaskMapper taskMapper;
    @Resource
    private QuartzManager quartzManager;

    @Override
    public IPage<Task> getListByPage(Page<Task> page, Task query) {
        return taskMapper.selectPage(page,new LambdaQueryWrapper<Task>().eq(Task::getCreatedBy,query.getCreatedBy()));
    }

    @Override
    public void initSchedule() {
        List<Task> list = this.list();
        for (Task task : list) {
            quartzManager.addJob(task);
        }
    }

    @Override
    public Boolean updateTaskById(Task task) {
        Task oldTask = this.getById(task.getId());
        if (this.updateById(task)) {
            try {
                quartzManager.deleteJob(oldTask);
                quartzManager.addJob(task);
                return true;
            } catch (SchedulerException e) {
                logger.error("更新job任务表达式异常：{}", e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public Boolean deleteById(Long id) {
        Task task = this.getById(id);
        if (task.getStatus() == 0) {
            try {
                quartzManager.deleteJob(task);
            } catch (SchedulerException e) {
                logger.error("删除任务异常：{}", e.getMessage());
                return false;
            }
        }
        return this.removeById(id);
    }

    @Override
    public Boolean performOneById(Long id) {
        Task task = this.getById(id);
        try {
            quartzManager.runJobNow(task);
            return true;
        } catch (SchedulerException e) {
            logger.error("立即执行任务异常：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean performOrSuspendOneById(Long id, Integer status) {
        Task task = this.getById(id);
        task.setStatus(status);
        try {
            if (status == 1) {
                quartzManager.pauseJob(task);
            }
            if (status == 0) {
                quartzManager.resumeJob(task);
            }
        } catch (SchedulerException ex) {
            logger.error("修改任务状态异常：{}", ex.getMessage());
            return false;
        }
        return this.updateById(task);
    }

    @Override
    public Boolean saveTask(Task task) {
        if (this.save(task)) {
            quartzManager.addJob(task);
            return true;
        }
        return false;
    }
}
