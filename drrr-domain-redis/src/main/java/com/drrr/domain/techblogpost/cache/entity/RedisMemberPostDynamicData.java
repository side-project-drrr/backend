package com.drrr.domain.techblogpost.cache.entity;

import com.drrr.domain.like.entity.TechBlogPostLike;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "redisMemberPostDynamicData", timeToLive = 300)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisMemberPostDynamicData {
    @Id
    private Long memberId;
    private Long postId;

    public static List<RedisMemberPostDynamicData> from(final List<TechBlogPostLike> memberLikedPosts) {
        return memberLikedPosts.stream()
                .map(memberLikedPost -> RedisMemberPostDynamicData.builder()
                        .postId(memberLikedPost.getPost().getId())
                        .memberId(memberLikedPost.getMember().getId())
                        .build())
                .toList();
    }

    public static Set<Long> iterableToSet(final Iterable<RedisMemberPostDynamicData> memberPostDynamicData){
        return StreamSupport.stream(memberPostDynamicData.spliterator(),
                        false)
                .map(data -> data.postId)
                .collect(Collectors.toSet());
    }
}
