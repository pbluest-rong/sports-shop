package com.pblues.sportsshop.service;

import com.pblues.sportsshop.constant.SortBy;
import com.pblues.sportsshop.dto.response.PageResponse;
import com.pblues.sportsshop.dto.response.ProductResponse;

import java.math.BigDecimal;

public interface ProductService {
    PageResponse<ProductResponse> getProductsByCategorySlug(String categorySlug, int page, int size);

    ProductResponse getProductBySlug(String slug);

    PageResponse<ProductResponse> getProducts(
            String query,
            String categoryId,
            String brand,
            BigDecimal priceMin,
            BigDecimal priceMax,
            String productSize,
            String color,
            SortBy sortBy,
            int page,
            int size
    );
}
