package com.pblues.sportsshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QdrantDeleteRequest {
    private DeletePoints points;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeletePoints {
        private List<String> ids;
    }
}
