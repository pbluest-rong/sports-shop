package com.pblues.sportsshop.dto.response;

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
    private String title;
    private String slug;
    private String brand;
    private String categoryId;
    private String mainImage;
    private String shortDescription;
    private VariantResponse firstVariant;
    private Rating rating;
}
