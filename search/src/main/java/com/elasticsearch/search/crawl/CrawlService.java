package com.elasticsearch.search.crawl;

import com.elasticsearch.search.user.User;
import com.elasticsearch.search.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class CrawlService {
    @Autowired
    CrawlConfig config;
    @Autowired
    UserService userService;
    @Autowired
    CrawlSiteRepository repository;
    public CrawlSite add(CrawlSite site){
        User me = userService.getMe();
        if (me == null) {
            throw new AccessDeniedException("Not Logged In");
        }
        site.setUserId(me.getId());
        site.setCreated(new Date());
        return repository.save(site);
    }


    public void crawl(CrawlSite crawlSite) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForEntity(config.listUrl, crawlSite, Void.class);
        } catch (Exception e) {

        }
    }

    public void crawl(String id) {
        CrawlSite crawlSite = repository.findById(id).orElse(null);
        if(crawlSite == null)
            return;
        crawl(crawlSite);
    }

    public Page<CrawlSite> getMineSite(Pageable pageable) {
        User me = userService.getMe();
        if (me == null) {
            throw new AccessDeniedException("Not Logged In");
        }
        return repository.findByUserId(me.getId(), pageable);
    }
}
