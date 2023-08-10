package com.example.drrrbatch.quartz;

import com.example.drrrbatch.batch.vo.TechBlogCode;
import java.time.LocalDate;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;


@Slf4j
@RequiredArgsConstructor
public class CrawlingBatchExecutor extends QuartzJobBean {
    private final JobLauncher jobLauncher;
    private final Job crawlingJob;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("run crawling");
        Arrays.stream(TechBlogCode.values())
                .forEach(this::executeCrawling);
    }

    @SneakyThrows
    private void executeCrawling(TechBlogCode techBlogCode) {
        jobLauncher.run(crawlingJob, new JobParametersBuilder()
                .addLong("techBlogCode", techBlogCode.getId())
                .addString("jobStartedDate", LocalDate.now().toString())
                .toJobParameters()
        );
    }
}
