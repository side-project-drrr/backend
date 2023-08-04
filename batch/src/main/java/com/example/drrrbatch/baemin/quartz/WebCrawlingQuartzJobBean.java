package com.example.drrrbatch.baemin.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;


@Configuration
public class WebCrawlingQuartzJobBean extends QuartzJobBean {

    private final JobLauncher jobLauncher;
    private final Job webCrawlingJob;

    @Autowired
    public WebCrawlingQuartzJobBean(JobLauncher jobLauncher, Job webCrawlingJob) {
        this.jobLauncher = jobLauncher;
        this.webCrawlingJob = webCrawlingJob;
    }


    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        try {
            jobLauncher.run(webCrawlingJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
