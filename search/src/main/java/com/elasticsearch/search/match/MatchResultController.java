package com.elasticsearch.search.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;


@RestController
@RequestMapping("matched")
public class MatchResultController {
    @Autowired
    MatchService matchService;

    @CrossOrigin
    @RequestMapping(value = "{id}/status", method = RequestMethod.POST)
    public void matchResultStatus(@PathVariable String id, String status){
        if(!MatchStatus.Statuses.contains(status))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        matchService.setMatchResultSumStatus(id, status);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Page<MatchResultSum> getMatch(@RequestBody MatchResultSumSearch search, Pageable pageable) {
        return matchService.searchMatchResult(search, pageable);
    }
}
