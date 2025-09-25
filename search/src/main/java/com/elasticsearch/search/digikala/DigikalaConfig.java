package com.elasticsearch.search.digikala;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("digikala")
@Data
public class DigikalaConfig {
    String productSearchUrl;
    String productDetailUrl;
}
