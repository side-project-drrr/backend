package com.drrr.domain.techblogpost.service;


import com.drrr.core.code.techblog.TechBlogCode;
import com.drrr.core.date.DateRangeBound;
import com.drrr.domain.techblogpost.entity.TemporalTechBlogPost;
import com.drrr.domain.techblogpost.entity.TemporalTechPostTag;
import com.drrr.domain.techblogpost.repository.TemporalTechBlogPostRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchTemporaryTechBlogPostService {

    private final TemporalTechBlogPostRepository temporalTechBlogPostRepository;

    public List<SearchTemporaryTechBlogPostResultDto> execute(
            final SearchTemporaryTechBlogPostDto searchTemporaryTechBlogPostDto) {
        return temporalTechBlogPostRepository.findBy(
                        searchTemporaryTechBlogPostDto.dateRangeBound(),
                        searchTemporaryTechBlogPostDto.assignTagCompleted(),
                        searchTemporaryTechBlogPostDto.pageable()
                ).stream()
                .map(SearchTemporaryTechBlogPostResultDto::from)
                .toList();
    }


    @Builder
    public record SearchTemporaryTechBlogPostDto(
            DateRangeBound dateRangeBound,
            Pageable pageable,
            Boolean assignTagCompleted
    ) {

    }

    @Builder
    public record SearchTemporaryTechBlogPostResultDto(
            Long id,
            LocalDate createdDate,
            String author,
            String thumbnailUrl,
            String title,
            String summary,
            String urlSuffix,
            String url,
            TechBlogCode techBlogCode,
            LocalDate crawledDate,
            boolean registrationCompleted,
            List<PostTag> postTags
    ) {

        public static SearchTemporaryTechBlogPostResultDto from(final TemporalTechBlogPost temporalTechBlogPost) {
            return SearchTemporaryTechBlogPostResultDto.builder()
                    .id(temporalTechBlogPost.getId())
                    .createdDate(temporalTechBlogPost.getCreatedDate())
                    .author(temporalTechBlogPost.getAuthor())
                    .thumbnailUrl(temporalTechBlogPost.getThumbnailUrl())
                    .title(temporalTechBlogPost.getTitle())
                    .summary(temporalTechBlogPost.getSummary())
                    .urlSuffix(temporalTechBlogPost.getUrlSuffix())
                    .url(temporalTechBlogPost.getUrl())
                    .techBlogCode(temporalTechBlogPost.getTechBlogCode())
                    .crawledDate(temporalTechBlogPost.getCrawledDate())
                    .registrationCompleted(temporalTechBlogPost.isRegistrationCompleted())
                    .postTags(temporalTechBlogPost.getTemporalTechPostTags().stream().map(PostTag::from).toList())
                    .build();

        }
    }

    @Builder
    public record PostTag(
            Long id,
            String displayName,
            String uniqueName
    ) {
        public static PostTag from(final TemporalTechPostTag temporalTechPostTag) {
            return PostTag.builder()
                    .id(temporalTechPostTag.getId())
                    .displayName(temporalTechPostTag.getCategory().getDisplayName())
                    .uniqueName(temporalTechPostTag.getCategory().getUniqueName())
                    .build();

        }

    }
}
