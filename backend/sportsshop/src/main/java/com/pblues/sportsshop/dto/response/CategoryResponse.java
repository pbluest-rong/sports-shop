package com.pblues.sportsshop.dto.response;

import com.pblues.sportsshop.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private String slug;
    private String imageUrl;
    private Integer level;
    private Integer order;
    private String parentId;
    private String path;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private boolean hasChildren;

    public static CategoryResponse mapperToCategoryResponse(Category category, boolean hasChildren) {
        return new CategoryResponse(
                category.getId() != null ? category.getId().toHexString() : null,
                category.getName(),
                category.getSlug(),
                category.getImageUrl(),
                category.getLevel(),
                category.getOrder(),
                category.getParentId() != null ? category.getParentId().toHexString() : null,
                category.getPath(),
                category.getIsActive(),
                category.getCreatedAt(),
                hasChildren
        );
    }
}
