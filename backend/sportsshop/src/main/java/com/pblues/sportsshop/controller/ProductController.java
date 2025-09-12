package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.common.constant.SortBy;
import com.pblues.sportsshop.dto.response.*;
import com.pblues.sportsshop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(@RequestParam String query,
                                              @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                              @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        PageResponse<ProductResponse> response = productService.vectorSearch(query, page, size);
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }

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

    @GetMapping("/categories/{*path}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable("path") String path,
                                                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        PageResponse<ProductResponse> response = productService.getProductsByCategoryPath(path, page, size);
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse> getProductBySlug(@PathVariable("slug") String slug) {
        return ResponseEntity.ok().body(ApiResponse.success("success", productService.getProductBySlug(slug)));
    }

    @GetMapping("/{slug}/variants")
    public ResponseEntity<ApiResponse> getVariants(@PathVariable String slug) {
        ProductVariantResponse response = productService.getProductVariants(slug);
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }

    @GetMapping("/{slug}/variant")
    public ResponseEntity<ApiResponse> getVariant(
            @PathVariable String slug,
            @RequestParam Map<String, Object> selectedAttributes) {

        VariantSelectionResponse response = productService.getVariantForSelection(slug, selectedAttributes);
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }
}