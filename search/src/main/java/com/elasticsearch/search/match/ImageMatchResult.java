package com.elasticsearch.search.match;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "similarity_cache")
@Data
public class ImageMatchResult {
    @Id
    private String id;
    @Field(type= FieldType.Text)
    private String srcUrl;
    @Field(type= FieldType.Text)
    private String dstUrl;
    @Field(type = FieldType.Double)
    private Double similarity;

    public ImageMatchResult(String srcUrl, String dstUrl, Double similarity) {
        this.srcUrl = srcUrl;
        this.dstUrl = dstUrl;
        this.similarity = similarity;
    }
    public ImageMatchResult(){

    }
}
