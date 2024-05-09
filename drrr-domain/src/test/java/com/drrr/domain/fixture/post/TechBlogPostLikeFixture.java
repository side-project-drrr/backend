package com.drrr.domain.fixture.post;

import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.util.ArrayList;
import java.util.List;

public class TechBlogPostLikeFixture {
    private final static int DEFAULT_LIKES = 100;
    private final static int DEFAULT_VIEW = 100;

    public static List<TechBlogPostLike> createTechBlogPostLikeIncrease(final List<Member> members,
                                                                        final TechBlogPost post) {
        return members.stream()
                .map(member -> {
                    return TechBlogPostLike.builder()
                            .member(member)
                            .post(post)
                            .build();
                }).toList();

    }
    public static List<TechBlogPostLike> createTechBlogPostsLikeIncrease(final List<Member> members,
                                                                        final List<TechBlogPost> posts) {
        List<TechBlogPostLike> techBlogPostLikesList = new ArrayList<>();
        members.forEach(member -> {
                    List<TechBlogPostLike> techBlogPostLikes = posts.stream()
                            .map(post -> {
                                return TechBlogPostLike.builder()
                                        .member(member)
                                        .post(post)
                                        .build();
                            }).toList();

                    techBlogPostLikesList.addAll(techBlogPostLikes);
                });
        return techBlogPostLikesList;
    }
}
