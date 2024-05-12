package com.drrr.config.util;


import com.drrr.config.util.SpringBatchTestSupport.FakeConfiguration;
import com.drrr.fluent.cralwer.core.WebDriverPool;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.WebDriver;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
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
@Import(FakeConfiguration.class)
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

    protected void launchJob(final String jobName) {
        final Job job = applicationContext.getBean(jobName, Job.class);
        jobLauncherTestUtils.setJob(job);

        try {
            jobLauncherTestUtils.launchJob();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @TestConfiguration
    static class FakeConfiguration {


        @Primary
        @Bean
        WebDriverPool fakeWebDriverPool() {
            return new WebDriverPool(new BasePooledObjectFactory<>() {
                @Override
                public WebDriver create() {
                    return new FakeDriver();
                }

                @Override
                public PooledObject<WebDriver> wrap(WebDriver obj) {
                    return new DefaultPooledObject<>(obj);
                }
            });
        }
    }

}
