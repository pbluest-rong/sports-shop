package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.dto.response.ApiResponse;
import com.pblues.sportsshop.dto.response.CategoryResponse;
import com.pblues.sportsshop.model.Category;
import com.pblues.sportsshop.service.product.CategoryService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/categories")
@RequiredArgsConstructor
public class AdminController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(@RequestBody Category category) {
        CategoryResponse response = categoryService.createCategory(category);
        return ResponseEntity.ok()
                .body(ApiResponse.success("Category created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable String id,
                                                      @RequestBody Category categoryDetails) {
        CategoryResponse updatedCategory = categoryService.updateCategory(new ObjectId(id), categoryDetails);
        return ResponseEntity.ok().body(ApiResponse.success("Category updated successfully", updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable String id) {
        boolean deleted = categoryService.deleteCategory(new ObjectId(id));
        return ResponseEntity.ok().body(ApiResponse.success("Category deleted successfully", deleted));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable String id) {
        CategoryResponse response = categoryService.getCategoryById(new ObjectId(id));
        return ResponseEntity.ok().body(ApiResponse.success("Category found", response));
    }
}
