package com.elasticsearch.search.crawl;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("crawl")
@Data
public class CrawlConfig {
    String listUrl;
}
