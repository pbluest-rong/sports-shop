package com.pblues.sportsshop.service;

import com.pblues.sportsshop.constant.SortBy;
import com.pblues.sportsshop.dto.response.VariantResponse;
import com.pblues.sportsshop.exception.ResourceNotFoundException;
import com.pblues.sportsshop.model.Category;
import com.pblues.sportsshop.model.Inventory;
import com.pblues.sportsshop.model.Product;
import com.pblues.sportsshop.model.subdocument.Variant;
import com.pblues.sportsshop.repository.CategoryRepository;
import com.pblues.sportsshop.repository.ProductCustomRepository;
import com.pblues.sportsshop.repository.ProductRepository;
import com.pblues.sportsshop.dto.response.PageResponse;
import com.pblues.sportsshop.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryService inventoryService;

    @Override
    public PageResponse<ProductResponse> getProductsByCategorySlug(String categorySlug, int page, int size) {
        Category category = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Product> products = productRepository.findByCategoryId(category.getId(), pageable);
        PageResponse<ProductResponse> response = mapProductsToDTO(products);
        return response;
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        Product p = productRepository.findBySlug(slug).orElse(null);
        return p != null ? mapProductToDTO(p) : null;
    }

    @Override
    public PageResponse<ProductResponse> getProducts(
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
    ) {
        Pageable pageable = createPageable(page, size, sortBy);

        ObjectId categoryObjectId = null;
        if (categoryId != null && !categoryId.isEmpty()) {
            try {
                categoryObjectId = new ObjectId(categoryId);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        Long priceMinLong = priceMin != null ? priceMin.multiply(BigDecimal.valueOf(100)).longValue() : null;
        Long priceMaxLong = priceMax != null ? priceMax.multiply(BigDecimal.valueOf(100)).longValue() : null;

        Page<Product> products = productCustomRepository.findProductsWithFilters(
                query, categoryObjectId, brand, priceMinLong, priceMaxLong,
                productSize, color, pageable
        );

        return mapProductsToDTO(products);
    }

    private PageResponse<ProductResponse> mapProductsToDTO(Page<Product> products) {
        List<ProductResponse> content = products.getContent().stream()
                .map(p -> {
                    String mainImage = null;
                    if (p.getVariants() != null && !p.getVariants().isEmpty()) {
                        List<String> imgs = p.getVariants().get(0).getImages();
                        if (imgs != null && !imgs.isEmpty()) {
                            mainImage = imgs.get(0);
                        }
                    }


                    List<VariantResponse> variantResponses = new ArrayList<>();
                    for (Variant v : p.getVariants()) {
                        Inventory inventory = inventoryService.getInventoryByVariant(p.getId(), v.getId());
                        VariantResponse variantResponse = VariantResponse.builder()
                                .id(v.getId())
                                .sku(v.getSku())
                                .attributes(v.getAttributes())
                                .images(v.getImages())
                                .displayOrder(v.getDisplayOrder())
                                .price(inventory.getPrice())
                                .stock(inventory.getAvailableStock())
                                .build();
                        variantResponses.add(variantResponse);
                    }

                    return ProductResponse.builder()
                            .id(p.getId().toHexString())
                            .title(p.getTitle())
                            .mainImage(mainImage)
                            .slug(p.getSlug())
                            .brand(p.getBrand())
                            .categoryId(p.getCategoryId().toHexString())
                            .description(p.getDescription())
                            .attributes(p.getAttributes())
                            .variants(variantResponses)
                            .rating(p.getRating())
                            .createdAt(p.getCreatedAt())
                            .updatedAt(p.getUpdatedAt())
                            .build();
                })
                .toList();
        return PageResponse.<ProductResponse>builder()
                .content(content)
                .number(products.getNumber())
                .size(products.getSize())
                .totalElements((int) products.getTotalElements())
                .totalPages(products.getTotalPages())
                .first(products.isFirst())
                .last(products.isLast())
                .build();
    }

    private ProductResponse mapProductToDTO(Product product) {
        String mainImage = null;
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            List<String> imgs = product.getVariants().get(0).getImages();
            if (imgs != null && !imgs.isEmpty()) {
                mainImage = imgs.get(0);
            }
        }
        List<VariantResponse> variantResponses = new ArrayList<>();
        for (Variant v : product.getVariants()) {
            Inventory inventory = inventoryService.getInventoryByVariant(product.getId(), v.getId());
            VariantResponse variantResponse = VariantResponse.builder()
                    .id(v.getId())
                    .sku(v.getSku())
                    .attributes(v.getAttributes())
                    .images(v.getImages())
                    .displayOrder(v.getDisplayOrder())
                    .price(inventory.getPrice())
                    .stock(inventory.getAvailableStock())
                    .build();
            variantResponses.add(variantResponse);
        }
        return ProductResponse.builder()
                .id(product.getId().toHexString())
                .title(product.getTitle())
                .mainImage(mainImage)
                .slug(product.getSlug())
                .brand(product.getBrand())
                .categoryId(product.getCategoryId().toHexString())
                .description(product.getDescription())
                .attributes(product.getAttributes())
                .variants(variantResponses)
                .rating(product.getRating())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private Pageable createPageable(int page, int size, SortBy sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        if (sortBy != null) {
            switch (sortBy) {
                case RELEVANT:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
                case NEWEST:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
                case PRICE_ASC:
                    sort = Sort.by(Sort.Direction.ASC, "variants.price");
                    break;
                case PRICE_DESC:
                    sort = Sort.by(Sort.Direction.DESC, "variants.price");
                    break;
                case RATING_DESC:
                    sort = Sort.by(Sort.Direction.DESC, "rating.avg");
                    break;
                case BEST_SELLING_DESC:
                default:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
            }
        }

        return PageRequest.of(page, size, sort);
    }
}
