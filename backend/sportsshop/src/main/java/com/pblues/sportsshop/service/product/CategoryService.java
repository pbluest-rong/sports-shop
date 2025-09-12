package com.pblues.sportsshop.service.product;

import com.pblues.sportsshop.common.constant.ErrorCode;
import com.pblues.sportsshop.common.exception.AppException;
import com.pblues.sportsshop.dto.response.CategoryResponse;
import com.pblues.sportsshop.dto.response.SimpleCategoryResponse;
import com.pblues.sportsshop.model.Category;
import com.pblues.sportsshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Value("${spring.application.sports-category-id}")
    private String sportsCategoryId;

    public List<CategoryResponse> getChildrenCategories(ObjectId id) {
        return categoryRepository.findByIdAndIsActiveTrue(id)
                .map(parent -> categoryRepository.findByParentIdAndIsActiveTrue(parent.getId())
                        .stream()
                        .map(child -> CategoryResponse.mapperToCategoryResponse(
                                child,
                                categoryRepository.findByParentIdAndIsActiveTrue(child.getId()).size() > 0
                        ))
                        .toList())
                .orElse(List.of());
    }

    public List<CategoryResponse> getCategoryDescendants(ObjectId id) {
        return categoryRepository.findByIdAndIsActiveTrue(id)
                .map(category -> categoryRepository.findActiveDescendants(category.getPath())
                        .stream()
                        .map(child -> CategoryResponse.mapperToCategoryResponse(
                                child,
                                categoryRepository.findByParentIdAndIsActiveTrue(child.getId()).size() > 0
                        ))
                        .toList())
                .orElse(List.of());
    }

    @Transactional
    public CategoryResponse createCategory(Category category) {
        category.setCreatedAt(LocalDateTime.now());
        if (category.getParentId() != null) {
            categoryRepository.findById(category.getParentId()).ifPresent(parent -> {
                category.setLevel(parent.getLevel() + 1);
                String newPath = parent.getPath() != null ?
                        parent.getPath() + "/" + parent.getSlug() :
                        parent.getSlug();
                category.setPath(newPath);
            });
        } else {
            category.setLevel(1);
            category.setPath(category.getSlug());
        }

        Category saved = categoryRepository.save(category);
        return CategoryResponse.mapperToCategoryResponse(saved,
                categoryRepository.findByParentIdAndIsActiveTrue(saved.getId()).size() > 0);
    }

    @Transactional
    public CategoryResponse updateCategory(ObjectId id, Category categoryDetails) {
        return categoryRepository.findById(id).map(category -> {
            if (categoryDetails.getName() != null) category.setName(categoryDetails.getName());
            if (categoryDetails.getSlug() != null) category.setSlug(categoryDetails.getSlug());
            if (categoryDetails.getImageUrl() != null) category.setImageUrl(categoryDetails.getImageUrl());
            if (categoryDetails.getOrder() != null) category.setOrder(categoryDetails.getOrder());
            if (categoryDetails.getIsActive() != null) category.setIsActive(categoryDetails.getIsActive());
            Category saved = categoryRepository.save(category);
            return CategoryResponse.mapperToCategoryResponse(saved,
                    categoryRepository.findByParentIdAndIsActiveTrue(saved.getId()).size() > 0);
        }).orElse(null);
    }

    @Transactional
    public boolean deleteCategory(ObjectId id) {
        return categoryRepository.findById(id).map(category -> {
            List<Category> children = categoryRepository.findByParentId(id);
            if (!children.isEmpty()) {
                throw new IllegalStateException("Cannot delete category with children");
            }
            category.setIsActive(false);
            categoryRepository.save(category);
            return true;
        }).orElse(false);
    }

    public List<CategoryResponse> getCategoryAncestors(ObjectId id) {
        return categoryRepository.findByIdAndIsActiveTrue(id)
                .filter(cat -> cat.getPath() != null)
                .map(cat -> Arrays.stream(cat.getPath().split("/"))
                        .filter(s -> !s.isBlank())
                        .map(slug -> categoryRepository.findBySlugAndIsActiveTrue(slug).orElse(null))
                        .filter(Objects::nonNull)
                        .map(child -> CategoryResponse.mapperToCategoryResponse(
                                child,
                                categoryRepository.findByParentIdAndIsActiveTrue(child.getId()).size() > 0
                        ))
                        .toList())
                .orElse(List.of());
    }

    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentIdAndIsActiveTrue(null)
                .stream()
                .map(child -> CategoryResponse.mapperToCategoryResponse(
                        child,
                        categoryRepository.findByParentIdAndIsActiveTrue(child.getId()).size() > 0
                ))
                .toList();
    }

    public CategoryResponse getCategoryById(ObjectId id) {
        return categoryRepository.findById(id).map(child -> CategoryResponse.mapperToCategoryResponse(
                child,
                categoryRepository.findByParentIdAndIsActiveTrue(child.getId()).size() > 0
        )).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public List<SimpleCategoryResponse> getSportCategories() {
        return categoryRepository.findByIdAndIsActiveTrue(new ObjectId(sportsCategoryId))
                .map(parent -> categoryRepository.findByParentIdAndIsActiveTrue(parent.getId())
                        .stream()
                        .map(child -> SimpleCategoryResponse.mapperToCategoryResponse(child))
                        .toList())
                .orElse(List.of());
    }
}
