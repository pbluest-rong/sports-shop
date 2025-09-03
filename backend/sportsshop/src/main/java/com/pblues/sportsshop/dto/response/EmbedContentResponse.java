package com.pblues.sportsshop.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class EmbedContentResponse {
    private Embedding embedding;

    @Data
    public static class Embedding {
        private List<Double> values;
    }
}