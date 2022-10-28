package com.caston.quartz.manager;

import com.caston.quartz.TaskConstants;
import com.caston.quartz.entity.Task;
import com.caston.quartz.execution.QuartzJobExecution;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class QuartzManager {
    @Resource
    private Scheduler scheduler;

    /**
     * 得到quartz任务类
     *
     * @param task 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(Task task) {
        return QuartzJobExecution.class;
    }

    /**
     * 添加任务job
     *
     * @param task :任务实体类
     * @version 1.0.0 2021/9/6 12:10
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public void addJob(Task task) {
        try {
            // 创建jobDetail实例， 绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定Job类
            Class<? extends Job> jobClass = getQuartzJobClass(task);
//            Class<? extends Job> jobClass = (Class<? extends Job>) (Class.forName(task.getBeanClass())).newInstance().getClass();
            // 配置任务名称和组构成任务key
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(task.getId(), task.getJobGroup())).build();
            // 定义调度规则
            // 使用cornTrigger规则
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(task.getJobName(), task.getJobGroup())
                    .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
                    .withSchedule(CronScheduleBuilder.cronSchedule(task.getCron())).startNow().build();
            jobDetail.getJobDataMap().put(TaskConstants.TASK_PROPERTIES, task);
            // 判断是否存在
            if (scheduler.checkExists(getJobKey(task.getId(), task.getJobGroup()))) {
                // 防止创建时存在数据问题 先移除，然后在执行创建操作
                scheduler.deleteJob(getJobKey(task.getId(), task.getJobGroup()));
            }
            // 把job和触发器注册到任务调度中
            scheduler.scheduleJob(jobDetail, trigger);
            if (task.getStatus() == 1) {
                pauseJob(task);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(TaskConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 暂停一个job
     *
     * @param task :任务实体类
     * @version 1.0.0 2021/9/6 12:10
     * @since 1.0.0
     */
    public void pauseJob(Task task) throws SchedulerException {
        scheduler.pauseJob(getJobKey(task.getId(), task.getJobGroup()));
    }

    /**
     * 恢复一个job
     *
     * @param task :任务实体类
     * @version 1.0.0 2021/9/6 12:10
     * @since 1.0.0
     */
    public void resumeJob(Task task) throws SchedulerException {
        scheduler.resumeJob(getJobKey(task.getId(), task.getJobGroup()));
    }

    /**
     * 删除一个job
     *
     * @param task :任务实体类
     * @version 1.0.0 2021/9/6 12:10
     * @since 1.0.0
     */
    public void deleteJob(Task task) throws SchedulerException {
        scheduler.deleteJob(getJobKey(task.getId(), task.getJobGroup()));
    }

    /**
     * 立即触发job
     *
     * @param task :任务实体类
     * @version 1.0.0 2021/9/6 12:10
     * @since 1.0.0
     */
    public void runJobNow(Task task) throws SchedulerException {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(TaskConstants.TASK_PROPERTIES, task);
        scheduler.triggerJob(getJobKey(task.getId(), task.getJobGroup()), dataMap);
    }

    /**
     * 更新job表达式
     *
     * @param task :任务实体类
     * @version 1.0.0 2021/9/6 12:10
     * @since 1.0.0
     */
    public void updateJobCron(Task task) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobName(), task.getJobGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron());
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        scheduler.rescheduleJob(triggerKey, trigger);
    }
}
