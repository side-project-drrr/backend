package com.example.drrrbatch.baemin.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class WebCrawlingDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        String condition = jobExecution.getExecutionContext().getString("condition");

        if (condition.equals("CONTINUE")) {
            return new FlowExecutionStatus("CONTINUE");
        }

        return new FlowExecutionStatus("COMPLETED");
    }
}
