#!/bin/sh

date='2024-01-02'

#./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=WebCrawlerBatchJob techBlogCode=101 requestDate=$date"
#./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=WebCrawlerBatchJob techBlogCode=102 requestDate=$date"
#./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=WebCrawlerBatchJob techBlogCode=103 requestDate=$date"
#./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=WebCrawlerBatchJob techBlogCode=104 requestDate=$date"
#


./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=WebCrawlerBatchJob techBlogCode=107 requestDate=$date"

#./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=migrationJob techBlogCode=101 requestDate=$date"
#./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=migrationJob techBlogCode=102 requestDate=$date"
#./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=migrationJob techBlogCode=103 requestDate=$date"
#./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=migrationJob techBlogCode=105 requestDate=$date"
