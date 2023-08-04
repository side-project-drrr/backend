package com.example.drrrbatch.baemin.quartz;

import java.text.ParseException;

import org.quartz.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail quartzJobDetail() {
        return JobBuilder.newJob(WebCrawlingQuartzJobBean.class)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger jobTrigger() throws ParseException {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(quartzJobDetail());
        //0 0 8,17 * * ?
        trigger.setCronExpression("0 * * * * ?");
        trigger.afterPropertiesSet();
        return trigger.getObject();
    }
}
