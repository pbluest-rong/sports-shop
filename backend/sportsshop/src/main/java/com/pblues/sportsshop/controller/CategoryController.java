package com.pblues.sportsshop.controller;

import com.cloudinary.Api;
import com.pblues.sportsshop.dto.response.ApiResponse;
import com.pblues.sportsshop.dto.response.CategoryResponse;
import com.pblues.sportsshop.dto.response.SimpleCategoryResponse;
import com.pblues.sportsshop.service.product.CategoryService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/root")
    public ResponseEntity<ApiResponse> getRootCategories() {
        List<CategoryResponse> categoryResponses = categoryService.getRootCategories();
        return ResponseEntity.ok().body(ApiResponse.success("roots", categoryResponses));
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse> getChildrenCategories(@PathVariable String id) {
        List<CategoryResponse> categoryResponses = categoryService.getChildrenCategories(new ObjectId(id));
        return ResponseEntity.ok().body(ApiResponse.success("Success", categoryResponses));
    }

    @GetMapping("/{id}/descendants")
    public ResponseEntity<ApiResponse> getCategoryDescendants(@PathVariable String id) {
        List<CategoryResponse> categoryResponses = categoryService.getCategoryDescendants(new ObjectId(id));
        return ResponseEntity.ok().body(ApiResponse.success("Success", categoryResponses));
    }

    @GetMapping("/{id}/ancestors")
    public ResponseEntity<ApiResponse> getCategoryAncestors(@PathVariable String id) {
        List<CategoryResponse> categoryResponses = categoryService.getCategoryAncestors(new ObjectId(id));
        return ResponseEntity.ok().body(ApiResponse.success("category ancestors", categoryResponses));
    }

    @GetMapping("/sports")
    public ResponseEntity<ApiResponse> getSportCategories() {
        List<SimpleCategoryResponse> categoryResponses = categoryService.getSportCategories();
        return ResponseEntity.ok().body(ApiResponse.success("sport categories", categoryResponses));
    }
}