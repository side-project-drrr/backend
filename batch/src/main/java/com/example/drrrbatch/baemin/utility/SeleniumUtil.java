package com.example.drrrbatch.baemin.utility;

import com.example.drrrbatch.baemin.code.TechBlogCode;
import com.example.drrrbatch.baemin.entity.TechBlog;
import com.example.drrrbatch.baemin.repository.TechBlogRepository;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static org.openqa.selenium.support.ui.ExpectedConditions.textToBe;

@Slf4j
@Component
public class SeleniumUtil {
    public LocalDateTime convertDate(String dateString) {
        // Use DateTimeFormatter with a pattern and Locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM.d.yyyy", Locale.ENGLISH);
        LocalDate localDate = LocalDate.parse(dateString, formatter);

        // Setting the default time (e.g. 00:00)
        LocalDateTime createdDate = localDate.atTime(LocalTime.MIDNIGHT);

        return createdDate;
    }

    public TechBlog fetchTechBlogFromWeb(WebElement item){
        WebElement aTag = item.findElement(By.tagName("a"));
        List<WebElement> spanTag = aTag.findElements(By.tagName("span"));
        List<WebElement> pTag = aTag.findElements(By.tagName("p"));
        String thumbnailUrl = "없음";

        if (spanTag.size() == 3) {
            thumbnailUrl = spanTag.get(2).findElement(By.tagName("img")).getAttribute("src");
        }

        String hrefContent = aTag.getAttribute("href");
        String suffix = Pattern.compile("\\d+")
                .matcher(hrefContent)
                .results()
                .map(MatchResult::group)
                .findFirst().get();

        return TechBlog.builder()
                .date(convertDate(spanTag.get(0).getText()))
                .author(spanTag.get(1).getText())
                .thumbnailUrl(thumbnailUrl)
                .title(aTag.findElement(By.tagName("h1")).getText())
                .summary(pTag.get(1).getText())
                .urlSuffix(suffix)
                .url(hrefContent)
                .techBlogCode(TechBlogCode.BAEMIN)
                .build();
    }

    public int getLastPage(WebDriver driver, WebDriverWait wait, int page) {
        int lastPage = 0;
        driver.get("https://techblog.woowahan.com/");

        // 끝 페이지로 이동
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("last")));
        WebElement target = driver.findElement(By.className("last"));
        target.click();

        //끝 페이지에서
        wait.until(ExpectedConditions.not(textToBe(By.className("current"), String.valueOf(1))));
        lastPage = Integer.parseInt(driver.findElement(By.className("current")).getText());
        System.out.println("lastPage = " + lastPage);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("first")));
        target = driver.findElement(By.className("first"));
        target.click();
        wait.until(ExpectedConditions.and(
                ExpectedConditions.visibilityOfElementLocated(By.className("current")),
                ExpectedConditions.textToBePresentInElementLocated(By.className("current"), String.valueOf(page))
        ));

        return lastPage;
    }


    public boolean moveToNextPage(WebDriver driver, WebDriverWait wait, int page) {
        WebElement targetPageElement = driver.findElement(
                By.cssSelector("a[title='" + page + " 쪽']"));

        // 요소를 클릭합니다.
        if (targetPageElement != null) {
            targetPageElement.click();
            System.out.println("현재 페이지:" + page);
            wait.until(ExpectedConditions.and(
                    ExpectedConditions.visibilityOfElementLocated(By.className("current")),
                    ExpectedConditions.textToBePresentInElementLocated(By.className("current"),
                            String.valueOf(page))
            ));
        } else {
            log.info("해당 페이지 요소를 찾을 수 없습니다.");
            return false;
        }
        return true;
    }

    public void removeUnusedBlog(Set<String> suffixSet, TechBlogRepository repository) {
        suffixSet.stream()
                .forEach(repository::deleteBySuffix);
    }
}
