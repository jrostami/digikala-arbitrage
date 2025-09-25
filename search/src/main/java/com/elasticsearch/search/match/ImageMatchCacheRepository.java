package com.elasticsearch.search.match;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ImageMatchCacheRepository extends ElasticsearchRepository<ImageMatchResult, String> {

    ImageMatchResult findBySrcUrlAndDstUrl(String srcUrl, String dstUrl);
}