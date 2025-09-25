package com.elasticsearch.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("similarity")
@Data
public class SimilarityConfig {
    String serverUrl;
    Double imageMatchThreshold;
    Boolean useCache;
    String type;
    Integer matchThreadPoolSize;
    Double weightMax;
    Double heightMax;
    Double widthMax;
    Double lengthMax;
    Double matchThreshold;
    Integer resultPerProduct;
    Double exactMatch;
}
