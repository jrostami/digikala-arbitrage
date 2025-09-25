package com.elasticsearch.search.crawl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("crawl")
public class CrawlController {
    @Autowired
    CrawlService crawlService;

    @PostMapping("")
    public void addSite(@RequestBody CrawlSite site) {

        crawlService.add(site);
    }

    @GetMapping("{id}/run")
    public void run(@PathVariable String id) {
        crawlService.crawl(id);
    }

    @GetMapping("")
    public Page<CrawlSite> getMine(Pageable pageable) {
        return crawlService.getMineSite(pageable);
    }
}
