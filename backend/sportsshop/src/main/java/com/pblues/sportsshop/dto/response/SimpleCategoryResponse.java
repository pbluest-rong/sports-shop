package com.pblues.sportsshop.dto.response;

import com.pblues.sportsshop.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCategoryResponse {
    private String id;
    private String name;
    private String path;
    private String imageUrl;

    public static SimpleCategoryResponse mapperToCategoryResponse(Category category) {
        return new SimpleCategoryResponse(
                category.getId() != null ? category.getId().toHexString() : null,
                category.getName(),
                category.getPath(),
                category.getImageUrl()
        );
    }
}
