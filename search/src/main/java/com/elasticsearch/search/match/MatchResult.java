package com.elasticsearch.search.match;

import com.elasticsearch.search.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@JsonPropertyOrder({ "titleSimilarity", "bestImageMatch" })
@Data
public class MatchResult {
    Double titleSimilarity;
    Product src;
    Product dst;
    @JsonIgnore
    List<ImageMatchResult> imageMatches;
    String type;

    public MatchResult(Double titleSimilarity, List<ImageMatchResult> imageMatches) {
        this.titleSimilarity = titleSimilarity * 100;
        this.imageMatches = imageMatches;
    }

    public Double getTitleSimilarity() {
        return titleSimilarity;
    }
    public Double getBestImageMatchSimilarity(){
        if(imageMatches == null || imageMatches.isEmpty())
            return null;
        return Collections.max(imageMatches.stream().filter(item -> item.getSimilarity() != null).map(ImageMatchResult::getSimilarity).collect(Collectors.toList()));
    }

    public ImageMatchResult getBestImageMatch(){
        Double similarity = getBestImageMatchSimilarity();
        if(similarity == null)
            return null;
        for (ImageMatchResult imageMatch : imageMatches) {
            if(similarity.equals(imageMatch.getSimilarity()))
                return imageMatch;
        }
        return null;

    }
    public static int compare(MatchResult a, MatchResult b){
        if(a.getBestImageMatchSimilarity() == null)
            return -1;
        if(b.getBestImageMatchSimilarity() == null)
            return -1;
        return b.getBestImageMatchSimilarity().compareTo(a.getBestImageMatchSimilarity());
    }
}
