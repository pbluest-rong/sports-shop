package com.pblues.sportsshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QdrantUpsertRequest {
    private List<Point> points;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Point {
        private String id;
        private List<Double> vector;
        private Map<String, Object> payload;
    }
}
