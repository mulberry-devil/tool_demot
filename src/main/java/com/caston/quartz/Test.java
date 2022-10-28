package com.caston.quartz;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("test")
public class Test {

    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public void test() throws JobExecutionException {
        logger.info("定时任务test()：" + new Date());
        System.out.println("11111111111111111111");
    }

    public void testOne() throws JobExecutionException {
        logger.info("定时任务testOne()：" + new Date());
    }

}
