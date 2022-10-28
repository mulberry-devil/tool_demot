package com.caston.quartz.listener;

import com.caston.quartz.service.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScheduleJobInitListener implements CommandLineRunner {
    @Resource
    private TaskService taskService;

    @Override
    public void run(String... args) throws Exception {
        try {
            taskService.initSchedule();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
