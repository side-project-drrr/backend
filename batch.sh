#!/bin/sh

date='2023-12-01'

./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=WebCrawlerBatchJob techBlogCode=100 requestDate=$date" &
./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=WebCrawlerBatchJob techBlogCode=101 requestDate=$date" &
./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=WebCrawlerBatchJob techBlogCode=102 requestDate=$date" &
./gradlew drrr-batch:bootRun --args="--spring.batch.job.name=WebCrawlerBatchJob techBlogCode=103 requestDate=$date" &
