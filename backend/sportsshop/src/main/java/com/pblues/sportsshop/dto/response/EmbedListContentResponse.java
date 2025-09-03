package com.pblues.sportsshop.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class EmbedListContentResponse {
    private List<Embedding> embeddings;

    @Data
    public static class Embedding {
        private List<Double> values;
    }
}