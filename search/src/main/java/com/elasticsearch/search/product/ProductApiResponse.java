package com.elasticsearch.search.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductApiResponse {
    Integer token;
    List<Product> products;

    public ProductApiResponse(Integer token, List<Product> products) {
        this.token = token;
        this.products = products;
    }
}
