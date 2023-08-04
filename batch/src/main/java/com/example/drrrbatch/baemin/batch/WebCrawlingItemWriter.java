package com.example.drrrbatch.baemin.batch;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class WebCrawlingItemWriter<T> extends JpaItemWriter<List<T>> implements StepExecutionListener  {
    private final JpaItemWriter<T> jpaItemWriter;


    @Transactional
    @Override
    public void write(Chunk<? extends List<T>> chunk) {
        List<? extends List<T>> itemsList = chunk.getItems();
        List<T> list = new ArrayList<>();
        for (List<T> baeMinEntities : itemsList) {
            for (T entity : baeMinEntities) {
                list.add(entity);
            }
        }

        jpaItemWriter.write(new Chunk<T>(list));

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Line Writer ended.");
        return ExitStatus.COMPLETED;
    }
}
