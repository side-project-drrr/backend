package com.drrr.domain.techblogpost.repository.common;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TechBlogPostQueryService {
    private final TechBlogPostRepository techBlogPostRepository;

    public TechBlogPost getTechBlogPostById(final Long postId) {
        return techBlogPostRepository.findById(postId).orElseThrow(() -> {
            log.error("게시물을 찾을 수 없습니다 => postId : {}", postId);
            return DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
        });
    }
}
