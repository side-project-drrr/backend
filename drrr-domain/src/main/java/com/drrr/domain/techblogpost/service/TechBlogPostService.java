package com.drrr.domain.techblogpost.service;

import com.drrr.core.exception.techblog.TechBlogException;
import com.drrr.core.exception.techblog.TechBlogExceptionCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TechBlogPostService {
    private final TechBlogPostRepository techBlogPostRepository;

    public List<TechBlogPost> findNoCachedTechBlogPosts(final List<TechBlogPost> postsInRedis, final List<Long> postIds){
        //추천해줘야 할 전체 게시물 ids
        final Set<Long> postIdSet = new HashSet<>(postIds);

        // redis에서 가져온 게시물 id를 제외한 나머지 게시물 ids
        List<Long> noCachedPostIds = postsInRedis.stream()
                .map(TechBlogPost::getId)
                .filter(postId -> !postIdSet.contains(postId))
                .collect(Collectors.toList());

        // 만약 noCachedPostIds가 비어있다면 postIdSet을 사용
        if(noCachedPostIds.isEmpty()){
            return techBlogPostRepository.findByIdIn(noCachedPostIds);
        }

        return new ArrayList<>();
    }

    public List<TechBlogPost> findTechBlogPostsByIds(List<Long> postIds){
        List<TechBlogPost> posts = techBlogPostRepository.findByIdIn(postIds);
        if(posts.size() == 0){
            log.error("기술블로그를 찾을 수 없습니다.");
            throw new TechBlogException(TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.getCode(), TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.getMessage());
        }
        return posts;
    }
}
