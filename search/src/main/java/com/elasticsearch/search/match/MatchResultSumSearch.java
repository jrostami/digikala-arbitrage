package com.elasticsearch.search.match;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.JsonData;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class MatchResultSumSearch {
    @Data
    public static class Range {
        Double min;
        Double max;

        public Boolean isValid() {
            return !(min == null && max == null);
        }
    }

    String src;
    String status;
    Range profit;
    Range srcPrice;
    Range dstPrice;
    Range roi;
    Long catId;
    Long brandId;
    String category;
    String brand;
    String digiId;

    public SearchRequest getQuery(Pageable pageable) {
        return SearchRequest.of(search -> {
                    search.index("match_results")
                            .from(pageable.getPageNumber() * pageable.getPageSize())
                            .size(pageable.getPageSize())
                            .query(q -> q
                                    .bool(b -> {
                                        if(digiId != null)
                                            b= this.addTerm(b, "dstId", digiId);
                                        if (brand != null)
                                            b = this.addMatch(b, "dstBrand", brand);
                                        if (category != null)
                                            b = this.addMatch(b, "dstCat", category);
                                        if (src != null) {
                                            b = this.addTerm(b, "src", src);
                                        }
                                        if (status != null) {
                                            b = this.addTerm(b, "status.keyword", status);
                                        }

                                        if (profit != null) {
                                            b = this.addFilterRange(b, "profit", profit);
                                        }
                                        if (srcPrice != null) {
                                            b = this.addFilterRange(b, "srcPrice", srcPrice);
                                        }
                                        if (dstPrice != null) {
                                            b = this.addFilterRange(b, "dstPrice", dstPrice);
                                        }
                                        if (roi != null)
                                            b = this.addFilterRange(b, "roi", roi);
                                        if (catId != null)
                                            b = this.addTerm(b, "dstCatId", catId);
                                        if (brandId != null)
                                            b = this.addTerm(b, "dstBrandId", brandId);

                                        return b;
                                    })
                            );


                    if(!pageable.getSort().isEmpty()){
                        search.sort(sort->{
                            for (Sort.Order order : pageable.getSort()) {
                                sort.field(f->f
                                        .field(order.getProperty())
                                        .order(order.isAscending()?SortOrder.Asc:SortOrder.Desc)
                                );
                            }
                            return sort;
                        });
                    }
                    return search;
                }
        );
    }

    BoolQuery.Builder addFilterRange(BoolQuery.Builder b, String fieldName, Range range) {
        if (range == null || !range.isValid())
            return b;
        return b.filter(f -> f.range(rg -> {
            rg.field(fieldName);
            if (range.getMin() != null)
                rg.gte(JsonData.of(range.getMin()));
            if (range.getMax() != null)
                rg.lte(JsonData.of(range.getMax()));
            return rg;
        }));

    }

    BoolQuery.Builder addTerm(BoolQuery.Builder b, String fieldName, String fieldValue) {
        return b.must(m -> m.term(t -> t.field(fieldName).value(fieldValue)));
    }

    BoolQuery.Builder addTerm(BoolQuery.Builder b, String fieldName, Long fieldValue) {
        return b.must(m -> m.term(t -> t.field(fieldName).value(fieldValue)));
    }

    BoolQuery.Builder addMatch(BoolQuery.Builder b, String fieldName, String fieldValue) {
        return b.must(m -> m.match(t -> t.field(fieldName).query(fieldValue)));
    }

}
