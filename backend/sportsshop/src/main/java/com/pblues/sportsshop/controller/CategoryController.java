package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.dto.response.CategoryResponse;
import com.pblues.sportsshop.dto.response.SimpleCategoryResponse;
import com.pblues.sportsshop.service.product.CategoryService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/root")
    public List<CategoryResponse> getRootCategories() {
        return categoryService.getRootCategories();
    }

    @GetMapping("/{id}/children")
    public List<CategoryResponse> getChildrenCategories(@PathVariable String id) {
        return categoryService.getChildrenCategories(new ObjectId(id));
    }

    @GetMapping("/{id}/descendants")
    public List<CategoryResponse> getCategoryDescendants(@PathVariable String id) {
        return categoryService.getCategoryDescendants(new ObjectId(id));
    }

    @GetMapping("/{id}/ancestors")
    public List<CategoryResponse> getCategoryAncestors(@PathVariable String id) {
        return categoryService.getCategoryAncestors(new ObjectId(id));
    }
    @GetMapping("/sports")
    public List<SimpleCategoryResponse> getSportCategories() {
        return categoryService.getSportCategories();
    }
}
