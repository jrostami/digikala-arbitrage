package com.elasticsearch.search.digikala;

import lombok.Data;

@Data
public class ProductDetailResult {
    @lombok.Data
    public static class Data{
        ProductSearch.Product product;
    }

    @lombok.Data
    public static class Variant{
        Price price;
    }

    @lombok.Data
    public static class MetaInfo{
        Long id;
        String title_fa;

    }

    @lombok.Data
    public static class Price{
        Long selling_price;
        Long rrp_price;

    }
    Integer status;
    Data data;
}
