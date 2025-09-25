package com.elasticsearch.search.user;

import com.elasticsearch.search.match.MatchResultSumSearch;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "save_search")
public class SaveSearch {
    @Id
    String id;
    @Field(type = FieldType.Object)
    MatchResultSumSearch search;
    @Field(type = FieldType.Text)
    String name;
    @Field(type = FieldType.Text)
    String userId;
    @Field(type = FieldType.Date)
    Date created;
}
