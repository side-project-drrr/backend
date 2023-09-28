package com.drrr.domain.recommend.service.data;

import com.drrr.core.code.Gender;
import com.drrr.core.code.TechBlogCode;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.log.entity.post.MemberPostLog;
import com.drrr.domain.log.repository.MemberTechBlogPostRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.entity.MemberRole;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DummyDataFactory {
    private final int CATEGORY_COUNT = 20;
    private final int MEMBER_COUNT = 100;
    private final int POST_COUNT = 100;
    private final int CATEGORY_WEIGHT_COUNT = 100;
    private final int MEMBER_POST_LOG_COUNT = 100;
    private final int CATEGORIES_PER_POST = 8;
    private final int MAX_PREFER_CATEGORIES_COUNT = 8;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TechBlogPostRepository techBlogPostRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryWeightRepository categoryWeightRepository;
    @Autowired
    private MemberTechBlogPostRepository memberTechBlogPostRepository;
    @Autowired
    private TechBlogPostCategoryRepository techBlogPostCategoryRepository;

    @Test
    public void 가짜_데이터_삽입() {
        insertMemberDummyData();
        insertPostDummyData();
        insertCategoryDummyData();
        insertCategoryWeightDummyData();
        insertMemberPostLogDummyData();
        insertPostCategoryDummyData();
    }


    private void insertMemberDummyData() {
        List<Member> members = IntStream.rangeClosed(1, MEMBER_COUNT).mapToObj(i -> {
            String email = "user" + i + "@example.com";
            String nickname = "user" + i;
            Gender gender = (i % 2 == 0) ? Gender.MAN : Gender.WOMAN; // 임의로 남성과 여성을 번갈아가며 설정
            String provider = "provider" + i;
            String providerId = "providerId" + i;
            String imageUrl = "http://example.com/image" + i + ".jpg";
            MemberRole role = MemberRole.USER; // 임의로 USER와 ADMIN을 번갈아가며 설정
            return Member.createMember(email, nickname, gender, provider, providerId, imageUrl, role);
        }).collect(Collectors.toList());
        memberRepository.saveAll(members);
    }

    private void insertPostDummyData() {
        List<TechBlogPost> techBlogPosts = IntStream.rangeClosed(1, POST_COUNT).mapToObj(i -> {
            //현재로부터 몇년전까지 랜덤으로 연월일을 뽑을지 정함
            int yearsBeforeRange = 2018;
            LocalDate createdDate = RandomLocalDate(yearsBeforeRange);
            String author = "Author" + i; // 짝수 인덱스에서만 저자 설정
            String thumbnailUrl = "http://example.com/thumbnail" + i + ".jpg";
            String title = "Title" + i;
            String summary = (i % 3 == 0) ? "Summary" + i : null; // 3의 배수 인덱스에서만 요약 설정
            String urlSuffix = "/suffix/" + i;
            String url = "http://example.com/suffix/" + i;
            int viewCount = getRandomValueInRange(Integer.class,100,5000);
            TechBlogCode techBlogCode = TechBlogCode.values()[i
                    % TechBlogCode.values().length]; // 순환적으로 TechBlogCode 값 할당
            return new TechBlogPost(createdDate, author, thumbnailUrl, title, summary, urlSuffix, url,
                    techBlogCode,viewCount);
        }).collect(Collectors.toList());
        techBlogPostRepository.saveAll(techBlogPosts);
    }

    private void insertCategoryDummyData() {
        List<Category> categories = IntStream.rangeClosed(1, CATEGORY_COUNT).mapToObj(i -> {
            String categoryName = "Category" + i;
            String categoryDisplayName = "Display Category" + i;
            return new Category(categoryName, categoryDisplayName);
        }).collect(Collectors.toList());
        categoryRepository.saveAll(categories);
    }

    private void insertCategoryWeightDummyData() {
        List<Member> members = memberRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<CategoryWeight> categoryWeights = new ArrayList<>();

        IntStream.range(0, CATEGORY_WEIGHT_COUNT).forEach(i -> {
            List<Integer> randomCategoryList = getRangeShuffledList(1, CATEGORY_COUNT);
            int preferCategoryCnt = getRandomValueInRange(Integer.class, 1, 8);
            Member member = members.get(i); // 순환적으로 Member 객체 할당
            IntStream.rangeClosed(1, preferCategoryCnt).forEach(j -> {
                Category category = categories.get(randomCategoryList.get(j));
                double value = getRandomValueInRange(Double.class, 0.0, 1.0); // 가중치 값 설정 (여기서는 단순히 인덱스에 0.01을 곱한 값을 사용)
                boolean preferred = true; // 짝수 인덱스에서만 선호 카테고리로 설정

                categoryWeights.add(new CategoryWeight(member, category, value, preferred));
            });
        });
        categoryWeightRepository.saveAll(categoryWeights);
    }

    private void insertMemberPostLogDummyData() {
        List<MemberPostLog> logs = new ArrayList<>();
        IntStream.range(0, MEMBER_POST_LOG_COUNT).forEach(i -> {
            List<Member> randomMemberIds = memberRepository.findAll();
            List<TechBlogPost> randomPostIds = techBlogPostRepository.findAll();
            Collections.shuffle(randomPostIds);
            Long randomMemberId = randomMemberIds.get(i).getId(); // 임의로 회원 ID 할당
            //멤버마다 몇개의 로그를 만들지 정함
            int createEachMemberLogCount = getRandomValueInRange(Integer.class, 10, 50);
            IntStream.range(0, createEachMemberLogCount).forEach(j -> {
                int randomBoolean1 = getRandomValueInRange(Integer.class, 1, 3);
                int randomBoolean2 = getRandomValueInRange(Integer.class, 1, 3);
                long randomPostId = randomPostIds.get(j).getId();
                boolean isRead = (randomBoolean1 == 1); // 짝수 인덱스에서만 읽음으로 설정
                boolean isRecommended = (randomBoolean2 == 1); // 3의 배수 인덱스에서만 추천으로 설정

                logs.add(new MemberPostLog(randomMemberId, randomPostId, isRead, isRecommended));
            });
        });
        memberTechBlogPostRepository.saveAll(logs);
    }


    private void insertPostCategoryDummyData() {

        List<TechBlogPost> posts = techBlogPostRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<TechBlogPostCategory> techBlogPostCategories1 = new ArrayList<>();
        Collections.shuffle(posts);
        IntStream.range(0, POST_COUNT).forEach(i -> {
            Collections.shuffle(categories);
            List<Integer> categoriesPerPost = getRangeShuffledList(1, CATEGORIES_PER_POST);
            TechBlogPost post = posts.get(i);
            int randomCategoryCount = getRandomValueInRange(Integer.class, 1, MAX_PREFER_CATEGORIES_COUNT);
            IntStream.rangeClosed(0, randomCategoryCount).forEach(j -> {
                Category category = categories.get(j);
                techBlogPostCategories1.add(new TechBlogPostCategory(post, category));
            });
        });

        techBlogPostCategoryRepository.saveAll(techBlogPostCategories1);
    }


    LocalDate RandomLocalDate(int yearFrom) {
        Random random = new Random();
        LocalDate now = LocalDate.now();
        LocalDate startYear = LocalDate.of(yearFrom, 1, 1);

        long daysBetween = ChronoUnit.DAYS.between(startYear, now);
        long randomDaysToSubtract = (long) (random.nextDouble() * daysBetween);

        return now.minusDays(randomDaysToSubtract);
    }

    public <T> T getRandomValueInRange(Class<T> wrapperClass, double startRange, double endRange) {
        Random random = new Random();
        double randomValue = startRange + (endRange - startRange) * random.nextDouble();

        if (wrapperClass.equals(Integer.class)) {
            return wrapperClass.cast((int) randomValue);
        } else if (wrapperClass.equals(Long.class)) {
            return wrapperClass.cast((long) randomValue);
        } else if (wrapperClass.equals(Double.class)) {
            return wrapperClass.cast(randomValue);
        } else if (wrapperClass.equals(Float.class)) {
            return wrapperClass.cast((float) randomValue);
        } else {
            throw new IllegalArgumentException("Unsupported wrapper class: " + wrapperClass.getName());
        }
    }

    public List<Integer> getRangeShuffledList(int start, int end) {
        List<Integer> shuffleList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            shuffleList.add(i);
        }
        Collections.shuffle(shuffleList);
        return shuffleList;
    }
}