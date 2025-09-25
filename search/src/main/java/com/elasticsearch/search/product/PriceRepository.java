package com.elasticsearch.search.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;


public interface PriceRepository extends ElasticsearchRepository<Price, String> {
    Page<Price> findByProductId(String productId, Pageable pageable);
}