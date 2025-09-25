package com.elasticsearch.search.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Document(indexName = "products")
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Product {
    @Id
    private String id;
    @Field(type = FieldType.Text)
    @NotBlank
    private String title;
    @Field(type = FieldType.Text)
    private String description;
    @Field(type=FieldType.Integer)
    private Long price;
    @Field(type=FieldType.Integer)
    private Long originalPrice;
    @Field(type = FieldType.Text)
    @NotBlank
    private String src;
    private Boolean availability;
    @Field(type = FieldType.Text)
    @NotBlank
    @URL
    private String link;
    @Field(type = FieldType.Text)
    private Long brandId;
    @Field(type = FieldType.Text)
    private String brand;
    @Field(type = FieldType.Text)
    private Long catId;
    @Field(type = FieldType.Text)
    private String cat;

    @Field(type = FieldType.Text)
    private List<@URL String> images;
    @Field(type = FieldType.Text)
    private String asin;
    @Field(type = FieldType.Text)
    private List<String> relatedProducts;


    @Field(type = FieldType.Date)
    Date productsUpdate;

    public Boolean existRelatedProduct(){
        return relatedProducts != null && !relatedProducts.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void mergeCheckNull(Product product){
        if(product.title != null)
            this.title = ProductService.convertPersianToEnglish(product.getTitle());
        if(product.description != null)
            this.description = product.getDescription();
        if(product.src != null)
            this.src = product.getSrc();
        if(product.link != null)
            this.link = product.getLink();
        if(product.brandId != null)
            this.brandId = product.getBrandId();
        if(product.brand != null)
            this.brand = product.getBrand();
        if(product.catId != null)
            this.catId = product.getCatId();
        if(product.cat != null)
            this.cat = product.getCat();
        if(product.images != null && !product.images.isEmpty())
            this.images = product.images;
        if(product.asin != null)
            this.asin = product.asin;
        if (product.price != null)
            this.price = product.price;
        if(product.originalPrice != null)
            this.originalPrice = product.originalPrice;
        if(product.relatedProducts != null)
            this.relatedProducts = product.relatedProducts;

        if(product.productsUpdate != null)
            this.productsUpdate = product.productsUpdate;

    }

    public void process() {
        if(title != null)
            title = ProductService.convertPersianToEnglish(title);
    }
}
