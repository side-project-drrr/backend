package com.example.drrrbatch.baemin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class RunBatchController {
    private final JobLauncher jobLauncher;

    private final Job myJob;

    @GetMapping("/run-batch")
    public ResponseEntity<String> runBatchJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("webCrawlingJob", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(myJob, jobParameters);

        return ResponseEntity.ok("Batch job has been invoked");
    }
}
