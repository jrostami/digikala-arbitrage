package com.elasticsearch.search.match;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.text.SimpleDateFormat;
import java.util.Date;


@Data
@Document(indexName = "match_results")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResultSum {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Id
    String id;
    @Field(type = FieldType.Text)
    String title;
    @Field(type = FieldType.Text)
    String dstTitle;
    @Field(type = FieldType.Text)
    String srcLink;
    @Field(type = FieldType.Text)
    String dstLink;
    @Field(type = FieldType.Text)
    String src;

    // price
    @Field(type = FieldType.Integer)
    Long srcPrice;
    @Field(type = FieldType.Integer)
    Long srcOrigPrice;
    @Field(type = FieldType.Integer)
    Long dstPrice;
    @Field(type = FieldType.Integer)
    Long dstOrigPrice;

    @Field(type = FieldType.Double)
    Double titleSimilarity;
    @Field(type = FieldType.Double)
    Double roi;
    @Field(type = FieldType.Long)
    Long profit;
    @Field(type = FieldType.Long)
    Long digiCommission;
    @Field(type = FieldType.Double)
    Double imageSimilarity;
    @Field(type = FieldType.Text)
    String srcImageLink;
    @Field(type = FieldType.Text)
    String dstImageLink;
    String status;
    @JsonIgnore
    @Field(type = FieldType.Date)
    Date updated;

    @JsonIgnore
    @Field(type = FieldType.Text)
    String srcId;
    @Field(type = FieldType.Text)
    String dstId;
    @Field(type = FieldType.Long)
    Long dstCatId;
    @Field(type = FieldType.Text)
    String dstCat;
    @Field(type = FieldType.Long)
    Long dstBrandId;
    @Field(type = FieldType.Text)
    String dstBrand;
    @Field(type = FieldType.Text)
    String matchType;
    @Field(type = FieldType.Date)
    Date created;




    public MatchResultSum() {

    }

    public void newUpdated() {
        updated = new Date();
    }

    public MatchResultSum(MatchResult matchResult, Long commission) {
        newUpdated();
        if (this.status == null)
            this.status = MatchStatus.PENDING;
        if (matchResult == null)
            return;
        this.matchType = matchResult.type;
        if (matchResult.src != null) {
            title = matchResult.src.getTitle();
            srcLink = matchResult.src.getLink();
            src = matchResult.src.getSrc();
            srcId = matchResult.src.getId();
            srcPrice = matchResult.src.getPrice();
            srcOrigPrice = matchResult.src.getOriginalPrice();
        }
        if (matchResult.dst != null) {
            this.dstId = matchResult.dst.getId();
            this.dstLink = matchResult.dst.getLink();
            this.dstTitle = matchResult.dst.getTitle();
            this.dstCat = matchResult.dst.getCat();
            this.dstCatId = matchResult.dst.getCatId();
            this.dstBrand = matchResult.dst.getBrand();
            this.dstBrandId = matchResult.dst.getBrandId();
            dstPrice = matchResult.dst.getPrice();
            dstOrigPrice = matchResult.dst.getOriginalPrice();
            this.digiCommission = commission;
        }
        titleSimilarity = matchResult.getTitleSimilarity();
        imageSimilarity = matchResult.getBestImageMatchSimilarity();
        if (matchResult.getBestImageMatch() != null) {
            srcImageLink = matchResult.getBestImageMatch().getSrcUrl();
            dstImageLink = matchResult.getBestImageMatch().getDstUrl();
        }
        created = new Date();
        this.calculateProfit();
    }

    public void calculateProfit(){
        if(dstPrice != null && srcPrice != null){
            profit = dstPrice - srcPrice - (digiCommission != null?digiCommission:0);
            roi = (double)profit/srcPrice;
        }
    }
    public String getStatus() {
        if (status == null)
            return MatchStatus.PENDING;
        return status;
    }

    public String getUpdatedAt() {
        if (updated == null)
            return null;
        return dateFormat.format(updated);
    }
    public Double getROIP(){
        if (roi != null)
        return roi * 100;
        return null;
    }


}
