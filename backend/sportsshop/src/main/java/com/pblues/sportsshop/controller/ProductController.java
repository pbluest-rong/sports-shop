package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.constant.SortBy;
import com.pblues.sportsshop.model.Product;
import com.pblues.sportsshop.response.ApiResponse;
import com.pblues.sportsshop.response.PageResponse;
import com.pblues.sportsshop.response.ProductResponse;
import com.pblues.sportsshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<ApiResponse> getProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String productSize,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) SortBy sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ProductResponse> response = productService.getProducts(
                query, categoryId, brand, priceMin, priceMax,
                productSize, color, sortBy, page, size
        );

        return ResponseEntity.ok().body(ApiResponse.success("Products retrieved successfully", response));
    }

    @GetMapping("/categories/{slug}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable("slug") String slug,
                                                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        PageResponse<ProductResponse> response = productService.getProductsByCategorySlug(slug, page, size);
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse> getProductBySlug(@PathVariable("slug") String slug) {
        return ResponseEntity.ok().body(ApiResponse.success("success", productService.getProductBySlug(slug)));
    }
}