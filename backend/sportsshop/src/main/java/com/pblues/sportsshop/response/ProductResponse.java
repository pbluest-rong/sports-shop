package com.pblues.sportsshop.response;

import com.pblues.sportsshop.model.subdocument.Rating;
import com.pblues.sportsshop.model.subdocument.Variant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String slug;
    private String brand;
    private String categoryId;
    private List<String> images;
    private String description;
    private Map<String, List<String>> attributes;
    private List<Variant> variants;
    private Rating rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
