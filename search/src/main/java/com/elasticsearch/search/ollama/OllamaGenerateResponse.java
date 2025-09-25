package com.elasticsearch.search.ollama;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OllamaGenerateResponse {
    String model;
    String response;

    @Data
    public static class OllamaSingleSimilarityResponse {
        Boolean sameProduct;
        String p1ModelNumber;
        String p2ModelNumber;
    }
    @Data
    public static class OllamaAllSimilarityResponse{
        List<LLMMatch> matches;
    }
    @Data
    public static class LLMMatch{
        String dstProductId;
        String dstProductTitle;
        Boolean sameProduct;
        String srcProductId;
        String srcModelNumber;
        String dstModelNumber;
    }

    OllamaAllSimilarityResponse getAllSimilarity(){
        if(response == null)
            return null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            //response = response.replaceAll("\\s+", " ");
            return objectMapper.readValue(response, OllamaAllSimilarityResponse.class);
        }catch (Exception e){
            return null;
        }
    }

    OllamaSingleSimilarityResponse getSimilarity(){
        if(response == null)
            return null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            //response = response.replaceAll("\\s+", " ");
            return objectMapper.readValue(response, OllamaSingleSimilarityResponse.class);
        }catch (Exception e){
            return null;
        }
    }

    List<String> getSimilarIds(){
        OllamaAllSimilarityResponse similarProducts = getAllSimilarity();
        if(similarProducts == null || similarProducts.matches == null || similarProducts.matches.isEmpty())
            return new ArrayList<>();
        return similarProducts.matches.stream().filter(item->item.sameProduct != null && item.sameProduct).map(item->item.dstProductId).collect(Collectors.toList());
    }
    Boolean isSimilar(){
        OllamaSingleSimilarityResponse similarity = getSimilarity();
        if(similarity == null)
            return false;
        if(similarity.sameProduct!= null && similarity.sameProduct)
            return true;
        return false;
    }
}
