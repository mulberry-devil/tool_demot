package com.caston.quartz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caston.quartz.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 定时任务信息表 服务类
 * </p>
 *
 * @author caston
 * @since 2022-10-27
 */
public interface TaskService extends IService<Task> {
    /**
     * 分页查询.
     *
     * @param page  :
     * @param query :
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.springboot.demo.entity.Task>
     * @since 1.0.0
     */
    IPage<Task> getListByPage(Page<Task> page, Task query);

    /**
     * 初始化加载任务.
     *
     * @since 1.0.0
     */
    void initSchedule();

    /**
     * 修改一个任务.
     *
     * @param task :任务信息
     * @return boolean
     * @since 1.0.0
     */
    Boolean updateTaskById(Task task);

    /**
     * 根据id删除一个任务.
     *
     * @param id :
     * @return boolean
     * @since 1.0.0
     */
    Boolean deleteById(Long id);

    /**
     * 根据id执行一次任务.
     *
     * @param id :id
     * @return boolean
     * @since 1.0.0
     */
    Boolean performOneById(Long id);

    /**
     * 根据id和状态判断执行或暂停一个任务.
     *
     * @param id     : id
     * @param status :状态 0为执行  1为暂停
     * @return boolean
     * @since 1.0.0
     */
    Boolean performOrSuspendOneById(Long id, Integer status);

    /**
     * 保存定时任务.
     * @param task :
     * @return java.lang.Boolean
     * @since 1.0.0
     */
    Boolean saveTask(Task task);
}
