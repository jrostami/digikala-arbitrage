package com.elasticsearch.search.product;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;


public interface ProductRepository extends ElasticsearchRepository<Product, String> {
    Product findProductByLink(String link);

    List<Product> findProductBySrc(String src);
}