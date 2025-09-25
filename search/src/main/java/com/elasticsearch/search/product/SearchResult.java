package com.elasticsearch.search.product;

import lombok.Data;

@Data
public class SearchResult<T> {
    T t;
    private Double similarity;
    private float score;

    public SearchResult(T t, Double similarity, float score) {
        this.t = t;
        this.similarity = similarity * 100;
        this.score = score;
    }
}
