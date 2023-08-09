package com.example.drrrbatch.baemin.batch;


import com.example.drrrbatch.baemin.entity.TechBlog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class WebCrawlingItemProcessor implements ItemProcessor<List<TechBlog>, List<TechBlog>>, StepExecutionListener {
    @Override
    public List<TechBlog> process(List<TechBlog> items) throws Exception {
        return items;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("==========Process Start=========");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("==========Process Complete=========");
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
