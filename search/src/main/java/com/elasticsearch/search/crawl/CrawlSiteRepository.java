package com.elasticsearch.search.crawl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface CrawlSiteRepository extends ElasticsearchRepository<CrawlSite, String> {
    Page<CrawlSite> findByUserId(String userId, Pageable pageable);
}