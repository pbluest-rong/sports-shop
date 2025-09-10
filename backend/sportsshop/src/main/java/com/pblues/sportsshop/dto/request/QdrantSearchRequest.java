package com.pblues.sportsshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QdrantSearchRequest {
    private List<Double> vector;
    private int limit;
}
