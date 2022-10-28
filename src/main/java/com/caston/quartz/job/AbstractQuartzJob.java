package com.caston.quartz.job;

import com.caston.quartz.TaskConstants;
import com.caston.quartz.entity.Task;
import com.caston.quartz.entity.TaskLog;
import com.caston.quartz.service.TaskLogService;
import com.caston.quartz.utils.BeansUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractQuartzJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static ConcurrentHashMap map = new ConcurrentHashMap<String,Date>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Task task = (Task) context.getMergedJobDataMap().get(TaskConstants.TASK_PROPERTIES);
        try {
            before(context, task);
            doExecute(context, task);
            after(context, task, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("定时任务执行异常：{}", ex.getMessage());
            after(context, task, ex);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param task    任务
     */
    protected void before(JobExecutionContext context, Task task) {
        map.put(task.getId(),new Date());
    }

    protected void after(JobExecutionContext context, Task task, Exception ex) {
        Date startTime = (Date) map.get(task.getId());

        TaskLog taskLog = new TaskLog();
        taskLog.setJobId(task.getId());
        taskLog.setJobGroup(task.getJobGroup());
        taskLog.setJobName(task.getJobName());
        taskLog.setBeanClass(task.getBeanClass());
        taskLog.setStartTime(startTime);
        taskLog.setEndTime(new Date());
        long runMs = taskLog.getEndTime().getTime() - taskLog.getStartTime().getTime();
        taskLog.setMessage(taskLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (null != ex) {
            taskLog.setStatus(1);
            taskLog.setExceptionInfo(ex.getMessage());
        } else {
            taskLog.setStatus(0);
        }
        BeansUtils.getBean(TaskLogService.class).save(taskLog);
    }

    /**
     * 执行方法
     *
     * @param context 工作执行上下文对象
     * @param task    系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, Task task) throws Exception;
}
