package com.caston.quartz.execution;

import com.caston.quartz.entity.Task;
import com.caston.quartz.job.AbstractQuartzJob;
import com.caston.quartz.utils.JobInvokeUtils;
import org.quartz.JobExecutionContext;

public class QuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, Task task) throws Exception {
        JobInvokeUtils.invokeMethod(task);
    }
}
