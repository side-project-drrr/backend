package com.drrr.config.util;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@SpringBatchTest
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false",
        "spring.jpa.show-sql=true"
})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class SpringBatchTestSupport {
    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ApplicationContext applicationContext;


    protected void launchJob(final String jobName, JobParameters jobParameters) {
        final Job job = applicationContext.getBean(jobName, Job.class);
        jobLauncherTestUtils.setJob(job);
        try {
            jobLauncherTestUtils.launchJob(jobParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
