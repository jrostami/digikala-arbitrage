package com.elasticsearch.search.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
public class AccessService {
    @Value("${accessKey.upload}")
    List<String> keys;

    public void checkKey(String key) {
        if (!keys.contains(key))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
    }
}
