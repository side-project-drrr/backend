package com.example.drrrbatch.baemin.batch;


import com.example.drrrbatch.baemin.code.TechBlogCode;
import com.example.drrrbatch.baemin.entity.TechBlog;
import com.example.drrrbatch.baemin.repository.TechBlogRepository;
import com.example.drrrbatch.baemin.utility.SeleniumUtil;
import com.example.drrrbatch.baemin.utility.WebDriverFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class WebCrawlingTasklet implements Tasklet, StepExecutionListener {
    private final TechBlogRepository repository;
    private final SeleniumUtil seleniumUtil;
    private final WebDriverFactory webDriverFactory;
    private int page;
    private int lastPage = 0;
    private WebDriver driver;
    private WebDriverWait wait;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        page = 1;
        Set<String> suffixSet = repository.findUrlSuffixByCode(TechBlogCode.BAEMIN)
                .stream().collect(Collectors.toSet());

        if (suffixSet.size() == 0) {
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
                    .put("condition", "CONTINUE");
            return RepeatStatus.FINISHED;
        }

        driver = webDriverFactory.createDriver();
        wait = webDriverFactory.createDriverWait(driver);
        lastPage = seleniumUtil.getLastPage(driver, wait, page);

        while (true) {

            page++;

            //해당 페이지의 블로그 글 읽어오기기
            WebElement postsElement = driver.findElement(By.className("posts"));
            List<WebElement> items = postsElement.findElements(By.className("item"));

            // 각 'item' 요소에서 a 태그의 내용을 가져옵니다.
            for (WebElement item : items) {

                String className = item.getAttribute("class");

                if (className.equals("item")) {

                    WebElement aTag = item.findElement(By.tagName("a"));

                    String suffix = Pattern.compile("\\d+")
                            .matcher(aTag.getAttribute("href"))
                            .results()
                            .map(MatchResult::group)
                            .findFirst().get();

                    //최신 블로그일 경우 엔티티 생성 후 저장
                    if (!suffixSet.contains(suffix)) {
                        TechBlog techBlog = seleniumUtil.fetchTechBlogFromWeb(item);
                        repository.save(techBlog);
                    } else {
                        //기존 생성된 블로그 게시물은 set에서 삭제 후 나중에 남은 것들은 db에서 삭제
                        suffixSet.remove(suffix);
                    }
                }
            }

            //마지막 페이지를 끝냈을 대 page는 이미 lastPage +1인 상태여서 여기서 break 다음 페이지로(moveToNextPage) 넘어가지 않기 위함
            if (page == lastPage + 1) {
                break;
            }

            //다음 페이지로 넘어가서 crawling
            boolean status = seleniumUtil.moveToNextPage(driver, wait, page);

            //페이지를 찾을 수 없음
            if (!status) {
                break;
            }
        }

        seleniumUtil.removeUnusedBlog(suffixSet, repository);

        //Socket Error 방지용
        webDriverFactory.closeDriver(driver);
        contribution.setExitStatus(ExitStatus.COMPLETED);
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
                .put("condition", "COMPLETED");
        return RepeatStatus.FINISHED;

    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("===========Tasklet Start=========");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("===========Tasklet Complete=========");
        stepExecution.setExitStatus(ExitStatus.COMPLETED);

        return ExitStatus.COMPLETED;
    }


}
