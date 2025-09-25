package com.elasticsearch.search.ollama;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class OllamaGenerateRequest {

    @Data
    public static class MiniProduct{
        String id;
        String title;
        public MiniProduct(String id, String title){
            this.id = id;
            this.title = title;
        }

        public String toString(){
            ObjectMapper objectMapper = new ObjectMapper();
            try{
                return objectMapper.writeValueAsString(this);
            }catch (Exception e){
                return "";
            }
        }
    }

    String model;
    String prompt;
    Boolean stream;
    String format;

    public OllamaGenerateRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
        this.format = "json";
        this.stream = false;
    }
}
