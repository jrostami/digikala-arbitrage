package com.elasticsearch.search.product;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    ElasticsearchOperations esTemplate;
    @Autowired
    private ProductRepository productRepository;

    static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }

    }

    public List<Product> saveAll(List<Product> products) {
        if (products == null || products.isEmpty())
            return new ArrayList<>();
        List<Product> savedProducts = new ArrayList<>();
        for (Product product : products) {

            if (product.getLink() == null && product.getId() == null)
                continue;
            product.process();
            Product savedProduct = getProduct(product);
            if (savedProduct == null)
                savedProduct = product;
            else
                savedProduct.mergeCheckNull(product);
            savedProducts.add(savedProduct);
        }

        Iterable<Product> products1 = productRepository.saveAll(savedProducts);
        List<Product> results = new ArrayList<>();
        products1.forEach(results::add);
        return results;
    }

    public Product getProduct(Product product) {
        if (product.getId() != null)
            return getProductById(product.getId());
        if (product.getLink() != null)
            return productRepository.findProductByLink(product.getLink());
        return null;
    }

    @Autowired
    ElasticsearchClient elasticsearchClient;

    public List<Product> searchProducts(String title, String src, int from, int size) {
        try {
            // Build the search request
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .from(from)
                    .size(size)
                    .query(q -> q
                            .bool(b -> b
                                    .must(m -> m
                                            .multiMatch(t -> t
                                                    .fields("title")
                                                    .query(ProductService.convertPersianToEnglish(title))

                                            )

                                    )
                                    .must(m -> m
                                            .match(t -> t
                                                    .field("src")  // Adjust field as needed
                                                    .query(src)  // Example value, adjust based on your needs
                                            )
                                    )
                            )
                    )
            );


            // Execute search
            SearchResponse<Product> searchResponse = elasticsearchClient.search(searchRequest, Product.class);

            // Extract hits
            List<Hit<Product>> hits = searchResponse.hits().hits();
            List<Product> products = new ArrayList<>();

            // Convert each hit to a Product and add to the list
            for (Hit<Product> hit : hits) {
                Product product = hit.source();  // Extract the Product object
                if (product != null) {
                    products.add(product);
                }
            }

            return products;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Product> getProducts(List<String> ids) {
        return Lists.newArrayList(productRepository.findAllById(ids));
    }

    public Product save(Product product) {
        product.process();
        Product saved = getProduct(product);
        if (saved == null)
            saved = product;
        else
            saved.mergeCheckNull(product);
        return productRepository.save(saved);
    }

    public Product justSave(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getProductBySrc(String src) {
        return productRepository.findProductBySrc(src);
    }

    public Product saveProduct(Product product) {
        product = save(product);
        return product;
    }

    public static String convertPersianToEnglish(String text) {
        // Persian numerals
        char[] persianDigits = {'\u06F0', '\u06F1', '\u06F2', '\u06F3', '\u06F4',
                '\u06F5', '\u06F6', '\u06F7', '\u06F8', '\u06F9'};

        // Arabic numerals
        char[] arabicDigits = {'\u0660', '\u0661', '\u0662', '\u0663', '\u0664',
                '\u0665', '\u0666', '\u0667', '\u0668', '\u0669'};

        // English numerals
        char[] englishDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        // Replace Persian/Arabic digits with English digits
        StringBuilder englishText = new StringBuilder();
        for (char c : text.toCharArray()) {
            boolean replaced = false;

            for (int i = 0; i < persianDigits.length; i++) {
                if (c == persianDigits[i] || c == arabicDigits[i]) {
                    englishText.append(englishDigits[i]);
                    replaced = true;
                    break;
                }
            }

            if (!replaced) {
                englishText.append(c); // Keep non-digit characters as is
            }
        }

        return englishText.toString();
    }
}
