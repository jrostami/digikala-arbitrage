package com.elasticsearch.search.match;

import java.util.Date;

public class Stats {
    Long threadId;
    Long numbers;
    Date date;

    public Stats(Long threadId) {
        this.threadId = threadId;
        this.numbers = 1L;
        date = new Date();
    }

    public synchronized void addStats(){
        numbers++;
        date= new Date();
    }
}
