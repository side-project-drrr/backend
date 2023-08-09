package com.example.drrrbatch.baemin.batch;

import com.example.drrrbatch.baemin.code.TechBlogCode;
import com.example.drrrbatch.baemin.entity.TechBlog;
import com.example.drrrbatch.baemin.repository.TechBlogRepository;
import com.example.drrrbatch.baemin.utility.SeleniumUtil;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBatchTest
@SpringBootTest
class BatchTest {
    @Autowired
    SeleniumUtil seleniumUtil;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils1;

    @Autowired
    private TechBlogRepository repository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private Job webCrawlingJob;
    @Test
    public void 배달의민족_기술블로그가_Crawling된다() throws Exception {
        //given

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }

    @Test
    public void 최신_배달의민족_기술블로그가_Crawling된다() throws Exception {
        //given
        JobParameters jobParameters1 = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils1.launchJob(jobParameters1);
        List<String> techBlogs = repository.findUrlSuffixByCode(TechBlogCode.BAEMIN);
        String suffix = techBlogs.get(0);

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                repository.deleteBySuffix(suffix);
            }
        });

        // when
        JobParameters jobParameters2 = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution2 = jobLauncherTestUtils.launchJob(jobParameters2);

        //then
        List<String> urlSuffixByCode = repository.findUrlSuffixByCode(TechBlogCode.BAEMIN);
        assertEquals(true,urlSuffixByCode.contains(suffix));
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        assertEquals("COMPLETED", jobExecution2.getExitStatus().getExitCode());

    }

    @Test
    public void 기존_배달의민족_기술블로그가_삭제된다() throws Exception {
        //given
        String suffix = "9999";
        TechBlog techBlog = TechBlog.builder()
                .date(seleniumUtil.convertDate("Jul.27.2023"))
                .author("홍길동")
                .thumbnailUrl("없음")
                .title("테스트 제목")
                .summary("테스트 요약")
                .urlSuffix(suffix)
                .url("https://techblog.woowahan.com/"+suffix+"/")
                .techBlogCode(TechBlogCode.BAEMIN)
                .build();

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                repository.save(techBlog);
            }
        });

        // when
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils1.launchJob(jobParameters);

        //then
        List<String> urlSuffixByCode = repository.findUrlSuffixByCode(TechBlogCode.BAEMIN);
        assertEquals(false,urlSuffixByCode.contains(suffix));
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

    }
}