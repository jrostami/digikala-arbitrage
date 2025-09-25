package com.elasticsearch.search.crawl;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "crawled_sites")
public class CrawlSite {

    @Data
    public static class Pattern {
        String product_list;
        String next_page;
        String link;
        String title;
        String price;
        String discount;
        String main_image;
        String availability;
        String description;
    }

    @Id
    String id;
    @Field(type = FieldType.Text)
    String shop_url;
    @Field(type = FieldType.Object)
    Pattern pattern;
    @Field(type = FieldType.Boolean)
    Boolean one_page;
    @Field(type = FieldType.Text)
    String userId;
    @Field(type = FieldType.Date)
    Date created;
}
