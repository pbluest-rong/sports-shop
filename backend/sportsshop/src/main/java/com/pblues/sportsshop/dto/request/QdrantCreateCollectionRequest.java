package com.pblues.sportsshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QdrantCreateCollectionRequest {
    private VectorsConfig vectors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VectorsConfig {
        private int size;
        private String distance;
    }
}
