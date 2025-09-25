package com.elasticsearch.search.match;

import com.google.common.collect.Lists;

import java.util.List;

public interface MatchStatus {
    String PENDING = "Pending";
    String REJECTED = "Rejected";
    String CONFIRMED = "Confirmed";
    String LEAD = "LEAD";
    List<String> Statuses = Lists.newArrayList(PENDING, CONFIRMED, LEAD, REJECTED);
}
