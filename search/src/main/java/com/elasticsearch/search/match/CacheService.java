package com.elasticsearch.search.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    ImageMatchCacheRepository cacheRepository;

    public ImageMatchResult getCache(String srcUrl, String dstUrl){
        ImageMatchResult result = cacheRepository.findBySrcUrlAndDstUrl(srcUrl, dstUrl);
        if(result == null)
            return cacheRepository.findBySrcUrlAndDstUrl(dstUrl, srcUrl);
        return result;
    }
    public void setCache(ImageMatchResult matchResult){
        cacheRepository.save(matchResult);
    }
    public void removeCache(String id){
        cacheRepository.deleteById(id);
    }
}
