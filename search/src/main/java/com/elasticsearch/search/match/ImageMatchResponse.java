package com.elasticsearch.search.match;

import lombok.Data;

@Data
public class ImageMatchResponse {
    private Double similarity;

    public ImageMatchResponse(){

    }
    public ImageMatchResponse(Double similarity){
        this.similarity = similarity;
    }
}
