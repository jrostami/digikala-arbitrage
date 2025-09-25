package com.elasticsearch.search.ollama;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ollama")
@Data
public class OllamaConfig {
    String model;
    String generateUrl;
    String similarityPrompt;
    String similarityPrompt2;
    String embeddingUrl;
}
