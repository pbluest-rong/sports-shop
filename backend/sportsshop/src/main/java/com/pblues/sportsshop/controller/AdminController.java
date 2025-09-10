package com.pblues.sportsshop.controller;

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
    public CategoryResponse createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable String id,
                                                   @RequestBody Category categoryDetails) {
        CategoryResponse updatedCategory = categoryService.updateCategory(new ObjectId(id), categoryDetails);
        return updatedCategory != null ?
                ResponseEntity.ok(updatedCategory) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id) {
        boolean deleted = categoryService.deleteCategory(new ObjectId(id));
        return deleted ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(new ObjectId(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
