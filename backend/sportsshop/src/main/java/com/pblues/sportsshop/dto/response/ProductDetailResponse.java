package com.pblues.sportsshop.dto.response;

import com.pblues.sportsshop.model.subdocument.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
    private String id;
    private String title;
    private String slug;
    private String brand;
    private String categoryId;
    private String mainImage;
    private String shortDescription;
    private String longDescription;
    private ProductVariantResponse variantInfo;
    private Rating rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
