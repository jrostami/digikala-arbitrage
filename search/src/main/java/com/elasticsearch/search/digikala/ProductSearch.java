package com.elasticsearch.search.digikala;

import lombok.Data;

import java.util.List;

@Data
public class ProductSearch {

    @lombok.Data
    public static class Image{
        List<String> webp_url;
    }
    @lombok.Data
    public static class Images{
        Image main;
        List<Image> list;
    }

    @lombok.Data
    public static class Product{
        String id;
        Images images;
        String title_fa;
        ProductDetailResult.Variant default_variant;
        ProductDetailResult.MetaInfo brand;
        ProductDetailResult.MetaInfo category;
    }


    @lombok.Data
    public static class Data {
        List<Product> products;
    }

    Integer status;
    Data data;
}
