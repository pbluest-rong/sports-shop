package com.pblues.sportsshop.service.product;

import com.pblues.sportsshop.common.constant.SortBy;
import com.pblues.sportsshop.dto.response.*;

import java.math.BigDecimal;
import java.util.Map;

public interface ProductService {
    PageResponse<ProductResponse> getProductsByCategoryPath(String categoryPath, int page, int size);

    ProductDetailResponse getProductBySlug(String slug);

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

    PageResponse<ProductResponse> vectorSearch(String query, int page, int size);

    ProductVariantResponse getProductVariants(String slug);

    VariantSelectionResponse getVariantForSelection(String slug, Map<String, Object> selectedAttributes);
}
