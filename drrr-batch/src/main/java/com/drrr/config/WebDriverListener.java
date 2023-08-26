package com.drrr.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
@RequiredArgsConstructor
public class WebDriverListener implements JobExecutionListener {

    private final WebDriver webDriver;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("====================Job Start===================");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("====================Job Complete===================");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (webDriver != null) {
            webDriver.quit();
        }
    }


}