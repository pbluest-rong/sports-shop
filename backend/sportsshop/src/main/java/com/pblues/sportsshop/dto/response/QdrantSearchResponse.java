package com.pblues.sportsshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QdrantSearchResponse {
    private List<Result> result;

    @Data
    public static class Result {
        private String id;
        private double score;
        private Map<String, Object> payload;
    }
}
