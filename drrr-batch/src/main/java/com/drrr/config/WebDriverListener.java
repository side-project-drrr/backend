package com.drrr.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
@RequiredArgsConstructor
public class WebDriverListener implements JobExecutionListener {


    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("====================Job Start===================");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("====================Job Complete===================");
    }


}