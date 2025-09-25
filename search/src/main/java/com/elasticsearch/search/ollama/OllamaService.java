package com.elasticsearch.search.ollama;

import com.elasticsearch.search.match.MatchService;
import com.elasticsearch.search.product.Product;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OllamaService {
    Logger logger = LoggerFactory.getLogger(OllamaService.class);
    @Autowired
    OllamaConfig ollamaConfig;

    //@PostConstruct
    public void init(){
        System.out.println("init");
        //getPossibleSimilarity("ست کامل شیر الات راسان مدل آتیس طلامات",
          //      "ست شیرآلات راسان مدل آتیس");
        //getPossibleSimilarity("ست کامل شیر الات راسان مدل آتیس طلامات",
            //    "ست شیرآلات راسان مدل صدف");
        String text1 = "This is the first text";
        String text2 = "This is the second text";

        // Fetch embeddings using RestTemplate
        double[] embedding1 = getEmbeddingFromOllama(text1);
        double[] embedding2 = getEmbeddingFromOllama(text2);

        // Calculate cosine similarity
        double similarity = cosineSimilarity(embedding1, embedding2);
        System.out.println("Cosine Similarity: " + similarity);


    }
    public Double cosineSimilarity(String titleA, String titleB){
        double[] embedding1 = getEmbeddingFromOllama(titleA);
        double[] embedding2 = getEmbeddingFromOllama(titleB);

        // Calculate cosine similarity
        double similarity = cosineSimilarity(embedding1, embedding2);
        return similarity * 100;
    }
    public Double cosineSimilarity(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vectors must have the same length.");
        }

        // Compute dot product and magnitudes
        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];           // Dot product
            magnitudeA += vec1[i] * vec1[i];           // Magnitude of vector A
            magnitudeB += vec2[i] * vec2[i];           // Magnitude of vector B
        }

        magnitudeA = Math.sqrt(magnitudeA);             // Square root of sum of squares for vector A
        magnitudeB = Math.sqrt(magnitudeB);             // Square root of sum of squares for vector B

        // Return cosine similarity
        return dotProduct / (magnitudeA * magnitudeB);
    }

    private double[] getEmbeddingFromOllama(String title) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        OllamaGenerateRequest request = new OllamaGenerateRequest(ollamaConfig.model, title);


        try {
            // Send POST request to Ollama API
            ResponseEntity<OllamaEmbeddingResponse> response = restTemplate.postForEntity(
                    ollamaConfig.embeddingUrl,
                    request,
                    OllamaEmbeddingResponse.class
            );
            if(response.getBody() == null)
                return null;
            return response.getBody().embedding;
        }catch (Exception e){
            return null;
        }

    }


    public List<Product> getSimilarProducts(Product src, List<Product> dsts){
        if(dsts == null || dsts.isEmpty())
            return new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();


        List<OllamaGenerateRequest.MiniProduct> products = dsts.stream().map(item -> new OllamaGenerateRequest.MiniProduct(item.getId(), item.getTitle())).collect(Collectors.toList());
        String productsString = StringUtils.join(products.stream().map(item->item.toString() + "\r\n").collect(Collectors.toList()));
        logger.info(src.getTitle());
        logger.info(productsString);
        String input = String.format(ollamaConfig.getSimilarityPrompt2(), src.getId(), src.getTitle(), productsString);
        OllamaGenerateRequest request = new OllamaGenerateRequest(ollamaConfig.model, input);
        try {
            ResponseEntity<OllamaGenerateResponse> response = restTemplate
                    .postForEntity(ollamaConfig.generateUrl, request , OllamaGenerateResponse.class);
            if(response.getBody() == null|| response.getBody().getResponse() == null)
                return null;
            List<String> similarIds = response.getBody().getSimilarIds();
            if(similarIds == null || similarIds.isEmpty())
                return new ArrayList<>();
            List<Product> productList = similarIds.stream().map(id -> {
                for (Product dst : dsts) {
                    if (dst.getId().equals(id))
                        return dst;
                }
                return null;
            }).filter(Objects::nonNull).toList();
            logger.info( src.getTitle());
            logger.info(StringUtils.join(productList.stream().map(item->item.getTitle() + "\n").collect(Collectors.toList())));
            return productList;
        }catch (Exception exception){
            return null;
        }
    }




    public Boolean getPossibleSimilarity(String titleA, String titleB){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        OllamaGenerateRequest request = new OllamaGenerateRequest(ollamaConfig.model, String.format(ollamaConfig.getSimilarityPrompt(), titleA, titleB));

        try {
            ResponseEntity<OllamaGenerateResponse> response = restTemplate
                    .postForEntity(ollamaConfig.generateUrl, request , OllamaGenerateResponse.class);
            if(response.getBody() == null|| response.getBody().getResponse() == null)
                return null;
            return response.getBody().isSimilar();
        }catch (Exception exception){
            return null;
        }
    }
}
