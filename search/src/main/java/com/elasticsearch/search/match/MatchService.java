package com.elasticsearch.search.match;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elasticsearch.search.config.SimilarityConfig;
import com.elasticsearch.search.digikala.DigikalaService;
import com.elasticsearch.search.ollama.OllamaService;
import com.elasticsearch.search.product.Product;
import com.elasticsearch.search.product.ProductService;
import info.debatty.java.stringsimilarity.Cosine;
import javassist.tools.web.BadHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {
    Logger logger = LoggerFactory.getLogger(MatchService.class);
    @Autowired
    private SimilarityConfig similarityConfig;

    @Autowired
    ProductService productService;

    @Autowired
    FileService fileService;
    @Autowired
    CacheService cacheService;
    @Autowired
    MatchResultSumRepository matchResultSumRepository;
    @Autowired
    OllamaService ollamaService;
    @Autowired
    ElasticsearchClient elasticsearchClient;


    public void setMatchResultSumStatus(String id, String status) {
        MatchResultSum matchResultSum = matchResultSumRepository.findById(id).orElse(null);
        if (matchResultSum == null)
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        matchResultSum.setStatus(status);
        matchResultSumRepository.save(matchResultSum);
    }

    public MatchResult matchProduct(String srcId, String dstId) throws BadHttpRequest {
        Product src = productService.getProductById(srcId);
        Product amazon = productService.getProductById(dstId);
        if (src == null || amazon == null)
            throw new BadHttpRequest();
        MatchResult matchResult = new MatchResult(getCosineSimilarity(src.getTitle(), amazon.getTitle()),
                matchProduct(src, amazon));
        matchResult.setSrc(src);
        matchResult.setDst(amazon);
        return matchResult;
    }

    public List<ImageMatchResult> matchProduct(Product src, Product dst) {
        if (src == null ||
                dst == null ||
                src.getImages() == null ||
                src.getImages().isEmpty() ||
                dst.getImages() == null ||
                dst.getImages().isEmpty()
        )
            return new ArrayList<>();
        List<String> srcImages = src.getImages();
        List<String> dstImages = dst.getImages();
        for (String srcLocalImage : srcImages) {
            fileService.downloadFileWithLocalCheck(srcLocalImage);
        }
        for (String dstLocalImage : dstImages) {
            fileService.downloadFileWithLocalCheck(dstLocalImage);
        }
        List<ImageMatchResult> results = new ArrayList<>();
        for (String srcImage : srcImages) {
            if (!fileService.urlFileExist(srcImage))
                continue;
            for (String dstImage : dstImages) {
                if (!fileService.urlFileExist(dstImage))
                    continue;

                ImageMatchResponse similarity = getSimilarity(srcImage, dstImage);
                if (similarity == null) {
                    System.out.println("null result in ");
                    try {
                        System.out.println(fileService.encodeUrl(srcImage));
                        System.out.println(fileService.encodeUrl(dstImage));
                    } catch (Exception e) {
                        System.out.println("Exception in encode");
                    }
                }
                if (similarity != null && similarity.getSimilarity() > similarityConfig.getImageMatchThreshold()) {
                    results.add(new ImageMatchResult(srcImage, dstImage, similarity.getSimilarity()));
                    if (similarity.getSimilarity() >= similarityConfig.getExactMatch())
                        break;
                }

            }
        }

        return results;
    }

    private Boolean useCache() {
        return similarityConfig.getUseCache() != null && similarityConfig.getUseCache();
    }

    public ImageMatchResponse getSimilarity(String src, String dst) {
        ImageMatchResult cache = cacheService.getCache(src, dst);
        if (cache != null && useCache())
            return new ImageMatchResponse(cache.getSimilarity());
        ImageMatchResponse similarity;
        if (similarityConfig.getType().equals("URL"))
            try {
                similarity = getSimilarityUrlBased(src, dst);
            } catch (Exception e) {
                similarity = null;
            }

        else {
            similarity = getSimilarity(new FileSystemResource(fileService.getUrlFileFullPath(src)),
                    new FileSystemResource(fileService.getUrlFileFullPath(dst)));
        }
        if (similarity != null && similarity.getSimilarity() != null) {
            cacheService.setCache(new ImageMatchResult(src, dst, similarity.getSimilarity()));
        }
        return similarity;
    }

    public ImageMatchResponse getSimilarity(FileSystemResource image1, FileSystemResource image2) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("images1", image1);
        body.add("images2", image2);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);
        ResponseEntity<ImageMatchResponse> response = restTemplate
                .postForEntity(similarityConfig.getServerUrl(), requestEntity, ImageMatchResponse.class);
        return response.getBody();
    }

    public ImageMatchResponse getSimilarityUrlBased(String src, String dst) throws UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("images1", fileService.encodeUrl(src));
        params.put("images2", fileService.encodeUrl(dst));
        while (true) {
            try {
                ImageMatchResponse response = restTemplate
                        .getForObject(similarityConfig.getServerUrl() + "?images1={images1}&images2={images2}", ImageMatchResponse.class, params);
                return response;

            } catch (ResourceAccessException e) {
                sleep(4000L);
                logger.info("image similarity isn't respond");
            } catch (Exception e) {
                return null;
            }
        }

    }

    public static void sleep(Long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (Exception e) {

        }
    }

    public Double getCosineSimilarity(String src, String dst) {
        if (src == null || dst == null)
            return 0d;
        Cosine cosine = new Cosine(2);
        return cosine.similarity(src, dst);
        //return ollamaService.cosineSimilarity(src, dst);
    }

    public Page<MatchResultSum> searchMatchResult(MatchResultSumSearch search, Pageable pageable) {
        try {
            SearchResponse<MatchResultSum> searchResponse = elasticsearchClient.search(search.getQuery(pageable), MatchResultSum.class);
            return convertToPage(searchResponse, pageable);
        } catch (Exception e) {
            return null;
        }
    }

    public Page<MatchResultSum> convertToPage(SearchResponse<MatchResultSum> searchResponse, Pageable pageable) {
        // Get the hits from the search response
        List<Hit<MatchResultSum>> hits = searchResponse.hits().hits();
        List<MatchResultSum> products = new ArrayList<>();

        // Convert each hit to a Product and add to the list
        for (Hit<MatchResultSum> hit : hits) {
            MatchResultSum product = hit.source();// Extract the Product object
            if (product != null) {
                product.setId(hit.id());
                products.add(product);
            }
        }// Get the total number of hits (total number of matching records)
        long totalHits = searchResponse.hits().total().value();

        // Create a Page object using PageImpl
        return new PageImpl<MatchResultSum>(products, pageable, totalHits);
    }

    public Boolean hasAlreadyProcessed(String srcId, String dstId) {
        MatchResultSum matchResultSum = matchResultSumRepository.findBySrcIdAndDstId(srcId, dstId);
        if (matchResultSum == null)
            return false;
        return MatchStatus.CONFIRMED.equals(matchResultSum.getStatus())
                || MatchStatus.REJECTED.equalsIgnoreCase(matchResultSum.getStatus());
    }

    public List<MatchResult> searchMatchWithRelatedProduct(Product product) {
        if (product.getRelatedProducts() == null || product.getRelatedProducts().isEmpty())
            return new ArrayList<>();
        List<Product> search = productService.searchProducts(product.getTitle(), DigikalaService.DIGIKALA_SRC, 0, 5);
        search = allAtOnceSimilarityCheck(product, search);
        if (search == null || search.isEmpty()) {
            logger.info(product.getTitle() + " match not found");
            return new ArrayList<>();
        }

        List<MatchResult> matchResults = search.stream().map(item -> {
                    if (hasAlreadyProcessed(product.getId(), item.getId()))
                        return null;
                    Double cosineSimilarity = getCosineSimilarity(product.getTitle(), item.getTitle());
                    MatchResult matchResult;

                    List<ImageMatchResult> imageMatches = matchProduct(product, item);
                    matchResult = new MatchResult(cosineSimilarity, imageMatches);
                    matchResult.setSrc(product);
                    matchResult.setDst(item);
                    matchResult.setType(SearchType.SEARCH_TITLE);
                    return matchResult;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        fileService.deleteFiles(product.getImages());
        for (Product item : search) {
            fileService.deleteFiles(item.getImages());
        }
        return matchResults;
    }

    public List<Product> allAtOnceSimilarityCheck(Product src, List<Product> dsts) {
        return ollamaService.getSimilarProducts(src, dsts);
    }

    public List<MatchResult> searchMatchWithId(String srcId) {
        Product productById = productService.getProductById(srcId);
        if (productById == null)
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        return searchMatchWithRelatedProduct(productById);
    }
}
