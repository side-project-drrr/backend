package com.example.drrrbatch.quartz;


import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {
    @Bean
    public JobDetail crawlingJobDetail() {
        return JobBuilder.newJob(CrawlingBatchExecutor.class)
                .withIdentity("CrawlingBatchExecutor")
                .storeDurably()
                .build();
    }

    @Bean
    public SimpleTrigger crawlingTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(crawlingJobDetail())
                .withIdentity("crawlingTrigger")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(1)
                        .repeatForever())
                .build();
    }


    @Bean
    public JobDetail migrationJobDetail(){
        return JobBuilder.newJob(MigrationBatchExecutor.class)
                .withIdentity("migrationBatchExecutor")
                .storeDurably()
                .build();

    }

    @Bean
    public SimpleTrigger migrationJob() {
        return TriggerBuilder.newTrigger()
                .forJob(migrationJobDetail())
                .withIdentity("migrationJObDetail")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(1)
                        .repeatForever())
                .build();
    }
}
