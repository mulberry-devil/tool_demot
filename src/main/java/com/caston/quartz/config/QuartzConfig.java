package com.caston.quartz.config;

import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfig {
    private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);

    private JobFactory jobFactory;

    public QuartzConfig(com.caston.quartz.factory.JobFactory jobFactory) {
        this.jobFactory = jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        try {
            factory.setSchedulerName("Scheduler");
            factory.setQuartzProperties(quartzProperties());
            // 延时启动
            factory.setStartupDelay(1);
            factory.setApplicationContextSchedulerContextKey("applicationContextKey");
            // 可选，QuartzScheduler
            // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
            factory.setOverwriteExistingJobs(true);
            factory.setJobFactory(jobFactory);
            // 设置自动启动，默认为true
            factory.setAutoStartup(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        // quartz参数
        Properties prop = new Properties();
        // 线程池配置
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "20");
        prop.put("org.quartz.threadPool.threadPriority", "5");
        return prop;
    }

    @Bean(name = "scheduler")
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }
}
