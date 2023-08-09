package com.example.drrrbatch.baemin.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

import java.text.ParseException;

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
        //0 0 8,17 * * * 매일 오전 8시 오후 5시에 한번 씩 실행
        trigger.setCronExpression("0 0 8,17 * * *");
        trigger.afterPropertiesSet();
        return trigger.getObject();
    }
}
