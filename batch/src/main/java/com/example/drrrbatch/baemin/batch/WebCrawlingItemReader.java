package com.example.drrrbatch.baemin.batch;

import com.example.drrrbatch.baemin.entity.TechBlog;
import com.example.drrrbatch.baemin.utility.SeleniumUtil;
import com.example.drrrbatch.baemin.utility.WebDriverFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebCrawlingItemReader implements ItemReader<List<TechBlog>>, StepExecutionListener {
    private final SeleniumUtil seleniumUtil;
    private final WebDriverFactory webDriverFactory;
    private int page = 1;
    private int lastPage = 0;
    private WebDriverWait wait;
    private WebDriver driver;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("=============Start Chunk===========");

        driver = webDriverFactory.createDriver();

        wait = webDriverFactory.createDriverWait(driver);

        lastPage = seleniumUtil.getLastPage(driver, wait, page);

    }

    @Override
    public List<TechBlog> read()
            throws Exception {
        //끝 페이지까지 다 crawling 마쳤다면 chunk 종료
        if (page == lastPage + 1) {
            return null;
        }

        List<TechBlog> baeminList = new ArrayList<>();

        page++;

        //해당 페이지의 블로그 글 읽어오기기
        WebElement postsElement = driver.findElement(By.className("posts"));
        List<WebElement> items = postsElement.findElements(By.className("item"));

        // 각 'item' 요소에서 a 태그의 내용을 가져옵니다.
        for (WebElement item : items) {
            String className = item.getAttribute("class");
            if (className.equals("item")) {
                TechBlog techBlog = seleniumUtil.fetchTechBlogFromWeb(item);
                baeminList.add(techBlog);
            }
        }

        //마지막 페이지를 끝냈을 대 page는 이미 lastPage +1인 상태여서 여기서 break
        if (page == lastPage + 1) {
            return baeminList;
        }

        //다음 페이지로 넘어가서 crawling
        seleniumUtil.moveToNextPage(driver, wait, page);

        return baeminList;
    }


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("=============Complete Chunk===========");
        webDriverFactory.closeDriver(driver);
        return ExitStatus.COMPLETED;
    }


}
