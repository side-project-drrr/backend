package com.drrr.reader.fluent;

import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.domain.ExternalBlogPosts;
import org.springframework.batch.item.ItemReader;

public interface TechBlogReader extends ItemReader<ExternalBlogPosts> {


    TechBlogCode getTechBlogCode();
}
