package com.elasticsearch.search.ollama;

import lombok.Data;

@Data
public class OllamaEmbeddingResponse {
    double[] embedding;
}
