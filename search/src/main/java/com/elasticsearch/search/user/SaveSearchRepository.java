package com.elasticsearch.search.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface SaveSearchRepository extends ElasticsearchRepository<SaveSearch, String> {
    SaveSearch findByName(String name);

    Page<SaveSearch> findByUserId(String userId, Pageable pageable);
}