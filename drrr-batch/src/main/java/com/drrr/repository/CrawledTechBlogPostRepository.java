package com.drrr.repository;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPost;
import com.drrr.domain.ExternalBlogPosts;
import java.util.Collection;
import java.util.HashMap;
import org.springframework.stereotype.Component;

@Component
public class CrawledTechBlogPostRepository {
    private final HashMap<Key, ExternalBlogPost> postHashMap = new HashMap<>();

    public void insert(ExternalBlogPost externalBlogPost) {
        this.postHashMap.put(Key.from(externalBlogPost), externalBlogPost);
    }

    public boolean containsKey(String suffix, TechBlogCode techBlogCode) {
        return this.postHashMap.containsKey(new Key(suffix, techBlogCode));
    }

    public boolean remove(String suffix, TechBlogCode techBlogCode) {
        return this.postHashMap.containsKey(new Key(suffix, techBlogCode));
    }

    public void remove(Key key) {
        this.postHashMap.remove(key);
    }

    public void clear() {
        this.postHashMap.clear();
    }

    public void insertAll(ExternalBlogPosts externalBlogPosts) {
        externalBlogPosts.posts().forEach(this::insert);
    }

    public int count() {
        return postHashMap.size();
    }

    public void ifPresentOrElse(Key key, Runnable presentRunnable, Runnable orElseRunner) {
        if (postHashMap.containsKey(key)) {
            presentRunnable.run();
            return;
        }
        orElseRunner.run();
    }

    public Collection<ExternalBlogPost> findAll() {
        return this.postHashMap.values();
    }

    public record Key(String suffix, TechBlogCode techBlogCode) {
        public static Key from(ExternalBlogPost externalBlogPost) {
            return new Key(externalBlogPost.suffix(), externalBlogPost.code());
        }
    }
}
