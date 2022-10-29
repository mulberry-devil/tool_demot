package com.caston.quartz.listener;

import com.caston.quartz.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScheduleJobInitListener implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(ScheduleJobInitListener.class);
    @Resource
    private TaskService taskService;

    @Override
    public void run(String... args) throws Exception {
        try {
            taskService.initSchedule();
        } catch (Exception ex) {
            log.error("(ScheduleJobInitListener.run)定时任务执行异常：{}", ex.getMessage());
        }
    }
}
