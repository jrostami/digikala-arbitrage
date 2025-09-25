package com.elasticsearch.search.match;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface MatchResultSumRepository extends ElasticsearchRepository<MatchResultSum, String> {
    MatchResultSum findBySrcIdAndDstId(String srcId, String dstId);
    MatchResultSum findBySrcIdAndStatus(String srcId, String status);

}