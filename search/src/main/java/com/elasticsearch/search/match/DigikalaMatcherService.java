package com.elasticsearch.search.match;

import com.elasticsearch.search.config.SimilarityConfig;
import com.elasticsearch.search.digikala.DigikalaService;
import com.elasticsearch.search.product.Product;
import com.elasticsearch.search.product.ProductService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class DigikalaMatcherService {
    // Millisec
    static Long waitProduct = 3 * 1000 * 60L;
    // Minute
    static Long relatedProductCacheMinutes = 60L;
    Logger logger = LoggerFactory.getLogger(DigikalaMatcherService.class);
    @Autowired
    SimilarityConfig similarityConfig;

    @Autowired
    MatchService matchService;
    @Autowired
    ProductService productService;
    @Autowired
    DigikalaService digikalaService;
    Map<Long, Stats> stats = new HashMap<>();
    @Autowired
    MatchResultSumRepository matchResultSumRepository;

    BlockingQueue<Product> productsQueue = new ArrayBlockingQueue<>(30000);

    public static void sleep(Long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (Exception e) {

        }
    }

    public void addProduct(Product product) {
        try {
            productsQueue.add(product);
        } catch (Exception e) {
        }
    }

    @PostConstruct
    public void initProductQueue() {

        new Thread(() -> {
            ExecutorService executorService = Executors.newFixedThreadPool(similarityConfig.getMatchThreadPoolSize());
            while (true) {
                try {

                    Product product = productsQueue.take();
                    executorService.execute(() -> {


                        processProduct(product);
                    });

                } catch (Exception e) {
                }
            }
        }).start();
    }

    private Boolean hasConfirmedMatch(Product product){
        MatchResultSum matchResultSum = matchResultSumRepository.findBySrcIdAndStatus(product.getId(), MatchStatus.CONFIRMED);
        return matchResultSum != null;
    }

    private void processProduct(Product product) {
        // TODO make sure this while end
        while (true) {
            try {
                logger.info("Processing product " + product.getTitle());
               if(hasConfirmedMatch(product)) {
                    logger.info(product.getId() + "already matched " + " load");
                    break;
                }
                String searchType = SearchType.SEARCH_TITLE;
                List<Product> products = searchProductOnDigikala(product);

                processSearchResult(product, products);
                List<MatchResult> matchResults = matchService.searchMatchWithRelatedProduct(product);

                List<MatchResultSum> productMatchResultSums = getProductMatchResultSums(
                        similarityConfig.getMatchThreshold(),
                        similarityConfig.getResultPerProduct(),
                        matchResults);
                saveMatchSumResult(productMatchResultSums);
                uploadAllMatches(productMatchResultSums);
                logger.info(product.getId() + " matched " + " load");
                break;
            } catch (Exception e) {
                logger.info("Regular Error main thread" + e.getMessage());
                sleep(20000L);
            }
        }
    }

    private void uploadAllMatches(List<MatchResultSum> matchResultSums) {
        for (MatchResultSum matchResultSum : matchResultSums) {
            while (true) {
                try {
                    uploadMatch(matchResultSum);
                    break;
                } catch (Exception e) {
                    logger.info("Upload matched to Bot error");
                    sleep(1000L);
                }
            }
        }
    }

    private void saveMatchSumResult(List<MatchResultSum> matchResultSums) {

        for (MatchResultSum matchResultSum : matchResultSums) {
            MatchResultSum bySrcIdAndDstId = matchResultSumRepository.findBySrcIdAndDstId(matchResultSum.srcId, matchResultSum.dstId);
            if (bySrcIdAndDstId != null)
                matchResultSum.setId(bySrcIdAndDstId.id);
        }
        List<List<MatchResultSum>> partition = Lists.partition(matchResultSums, 9000);
        for (List<MatchResultSum> resultSums : partition) {
            matchResultSumRepository.saveAll(resultSums);
        }

    }

    public Product processSearchResult(Product product, List<Product> products) {
        if (products != null && !products.isEmpty()) {
            products = productService.saveAll(products);
            product.setRelatedProducts(products.stream().map(Product::getId).collect(Collectors.toList()));
        }
        product.setProductsUpdate(new Date());
        return productService.justSave(product);
    }

    protected Boolean needToSearch(Product product) {

        LocalDateTime now = LocalDateTime.now();
        if (product.getProductsUpdate() != null) {
            LocalDateTime localDateTime = product.getProductsUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Duration between = Duration.between(localDateTime, now);
            return between.toMinutes() >= relatedProductCacheMinutes;
        }
        return true;
    }

    public List<Product> searchProductOnDigikala(Product product) {
        if (!needToSearch(product)) {
            return product.existRelatedProduct() ? productService.getProducts(product.getRelatedProducts()) : new ArrayList<>();
        }

        return digikalaService.searchDigikalaProducts(product.getTitle());
    }

    public List<MatchResultSum> getProductMatchResultSums(Double threshold, Integer resultPerProduct, List<MatchResult> matchResults) {

        resultPerProduct = (resultPerProduct != null ? resultPerProduct : 10);
        matchResults = summarizeMatchResult(threshold, resultPerProduct, matchResults);
        List<MatchResultSum> matchResultSums = new ArrayList<>();
        for (int i = 0; i < resultPerProduct; i++) {
            if (matchResults != null && matchResults.size() >= i + 1) {
                MatchResult matchResult = matchResults.get(i);
                MatchResultSum matchResultSum = new MatchResultSum(matchResult, digikalaService.getCommission(matchResult.dst));
                matchResultSums.add(matchResultSum);
            }
        }
        return matchResultSums;
    }


    private List<MatchResult> summarizeMatchResult(Double threshold, Integer resultPerProduct, List<MatchResult> results) {
        results = results.stream().filter(item -> item.getBestImageMatchSimilarity() != null).collect(Collectors.toList());
        Collections.sort(results, MatchResult::compare);
        if (threshold != null)
            results = results.stream().filter(item -> item.getBestImageMatchSimilarity() >= threshold).collect(Collectors.toList());
        if (resultPerProduct != null && results.size() >= resultPerProduct)
            results = results.subList(0, resultPerProduct);
        return results;
    }
    
    public void uploadMatch(MatchResultSum matchResultSum) {
        // TODO upload match
    }

}
