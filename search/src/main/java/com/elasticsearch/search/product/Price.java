package com.elasticsearch.search.product;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "product_price")
@Data
public class Price {
    @Id
    String id;
    @Field(type = FieldType.Text)
    String productId;
    @Field(type = FieldType.Long)
    Long price;
    @Field(type = FieldType.Boolean)
    Boolean availability;
    @Field(type = FieldType.Date)
    Date created;

    public Price(){

    }
    public Price(Product product){
        this.productId = product.getId();
        this.price = product.getPrice();
        this.availability = product.getAvailability();
        this.created = new Date();
    }
}
