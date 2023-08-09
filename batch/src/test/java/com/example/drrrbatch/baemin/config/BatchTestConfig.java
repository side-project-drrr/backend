package com.example.drrrbatch.baemin.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchTestConfig {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private Job webCrawlingJob;



    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils1() {
        JobLauncherTestUtils testUtils = new JobLauncherTestUtils();
        testUtils.setJobLauncher(jobLauncher);
        testUtils.setJobRepository(jobRepository);
        testUtils.setJob(webCrawlingJob);
        return testUtils;
    }
}
