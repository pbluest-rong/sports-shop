package com.pblues.sportsshop.service.product;

import com.pblues.sportsshop.common.constant.SortBy;
import com.pblues.sportsshop.dto.response.*;
import com.pblues.sportsshop.common.exception.ResourceNotFoundException;
import com.pblues.sportsshop.model.Category;
import com.pblues.sportsshop.model.Inventory;
import com.pblues.sportsshop.model.Product;
import com.pblues.sportsshop.model.subdocument.Variant;
import com.pblues.sportsshop.repository.CategoryRepository;
import com.pblues.sportsshop.repository.ProductCustomRepository;
import com.pblues.sportsshop.repository.ProductRepository;
import com.pblues.sportsshop.service.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryService inventoryService;
    private final ProductSearchService productSearchService;

    @Override
    public PageResponse<ProductResponse> getProductsByCategoryPath(String categoryPath, int page, int size) {
        Category category = categoryRepository.findByPathAndIsActiveTrue(categoryPath).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (categoryRepository.existsByParentIdAndIsActiveTrue(category.getId())) {
            List<Category> categories = categoryRepository.findActiveDescendants(categoryPath);

            List<ObjectId> categoryIds = categories.stream()
                    .map(Category::getId)
                    .toList();

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Product> products = productRepository.findActiveProductsByMultipleCategories(categoryIds, pageable);
            return mapProductsToDTO(products);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            Page<Product> products = productRepository.findByCategoryId(category.getId(), pageable);
            PageResponse<ProductResponse> response = mapProductsToDTO(products);
            return response;
        }
    }

    @Override
    public ProductDetailResponse getProductBySlug(String slug) {
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

    @Override
    public PageResponse<ProductResponse> vectorSearch(String query, int page, int size) {
        return mapProductsToDTO(productSearchService.vectorSearch(query, page, size));
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
                            .shortDescription(p.getShortDescription())
                            .firstVariant(variantResponses.get(0))
                            .rating(p.getRating())
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

    private ProductDetailResponse mapProductToDTO(Product product) {
        List<Variant> variants = product.getVariants();
        String mainImage = null;
        if (variants != null && !variants.isEmpty()) {
            List<String> imgs = variants.get(0).getImages();
            if (imgs != null && !imgs.isEmpty()) {
                mainImage = imgs.get(0);
            }
        }
        List<VariantResponse> variantResponses = variants.stream().map(v -> {
            Inventory inventory = inventoryService.getInventoryByVariant(product.getId(), v.getId());
            return VariantResponse.builder()
                    .id(v.getId())
                    .sku(v.getSku())
                    .attributes(v.getAttributes())
                    .images(v.getImages())
                    .displayOrder(v.getDisplayOrder())
                    .price(inventory.getPrice())
                    .stock(inventory.getAvailableStock())
                    .build();
        }).collect(Collectors.toList());

        VariantResponse defaultVariant = variantResponses.isEmpty() ? null : variantResponses.get(0);

        Map<String, List<ProductVariantResponse.AttributeOption>> attributes = new LinkedHashMap<>();

        if (!variantResponses.isEmpty()) {
            // Xác định thứ tự attribute
            List<String> attrOrder = new ArrayList<>(variantResponses.get(0).getAttributes().keySet());
            String firstAttr = attrOrder.get(0);
            String lastAttr = attrOrder.get(attrOrder.size() - 1);

            // Build attribute đầu tiên (có image)
            Map<Object, String> valueToImage = new LinkedHashMap<>();
            for (VariantResponse vr : variantResponses) {
                Object val = vr.getAttributes().get(firstAttr);
                String image = vr.getImages().isEmpty() ? "" : vr.getImages().get(0);
                // Nếu đã có value rồi thì bỏ qua, giữ ảnh đầu tiên làm đại diện
                valueToImage.putIfAbsent(val, image);
            }

            List<ProductVariantResponse.AttributeOption> firstOptions = valueToImage.entrySet().stream()
                    .map(e -> ProductVariantResponse.AttributeOption.builder()
                            .value(e.getKey())
                            .image(e.getValue())
                            .build())
                    .collect(Collectors.toList());

            attributes.put(firstAttr, firstOptions);

            // Build các attribute giữa (không cần stock, không cần image)
            for (int i = 1; i < attrOrder.size() - 1; i++) {
                String attrName = attrOrder.get(i);
                Set<Object> vals = new LinkedHashSet<>();
                for (VariantResponse vr : variantResponses) {
                    vals.add(vr.getAttributes().get(attrName));
                }
                List<ProductVariantResponse.AttributeOption> options = vals.stream()
                        .map(v -> ProductVariantResponse.AttributeOption.builder()
                                .value(v)
                                .build())
                        .collect(Collectors.toList());
                attributes.put(attrName, options);
            }

            // Build attribute cuối (stock dựa trên các attribute khác = defaultVariant)
            String lastAttrName = lastAttr;
            Map<Object, Integer> stockMap = new LinkedHashMap<>();
            for (VariantResponse vr : variantResponses) {
                boolean match = true;
                for (int i = 0; i < attrOrder.size() - 1; i++) {
                    String attrName = attrOrder.get(i);
                    Object defaultVal = defaultVariant.getAttributes().get(attrName);
                    if (!vr.getAttributes().get(attrName).equals(defaultVal)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    Object lastVal = vr.getAttributes().get(lastAttrName);
                    stockMap.put(lastVal, stockMap.getOrDefault(lastVal, 0) + vr.getStock());
                }
            }
            List<ProductVariantResponse.AttributeOption> lastOptions = stockMap.entrySet().stream()
                    .map(e -> ProductVariantResponse.AttributeOption.builder()
                            .value(e.getKey())
                            .stock(e.getValue())
                            .build())
                    .collect(Collectors.toList());
            attributes.put(lastAttrName, lastOptions);
        }

        ProductVariantResponse variantResponse = ProductVariantResponse.builder()
                .attributes(attributes)
                .defaultVariant(defaultVariant)
                .build();

        return ProductDetailResponse.builder()
                .id(product.getId().toHexString())
                .title(product.getTitle())
                .mainImage(mainImage)
                .slug(product.getSlug())
                .brand(product.getBrand())
                .categoryId(product.getCategoryId().toHexString())
                .shortDescription(product.getShortDescription())
                .longDescription(product.getLongDescription())
                .variantInfo(variantResponse)
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

    @Override
    public ProductVariantResponse getProductVariants(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Variant> variants = product.getVariants();

        // Build list VariantResponse
        List<VariantResponse> variantResponses = variants.stream().map(v -> {
            Inventory inventory = inventoryService.getInventoryByVariant(product.getId(), v.getId());
            return VariantResponse.builder()
                    .id(v.getId())
                    .sku(v.getSku())
                    .attributes(v.getAttributes())
                    .images(v.getImages())
                    .displayOrder(v.getDisplayOrder())
                    .price(inventory.getPrice())
                    .stock(inventory.getAvailableStock())
                    .build();
        }).collect(Collectors.toList());

        VariantResponse defaultVariant = variantResponses.isEmpty() ? null : variantResponses.get(0);

        Map<String, List<ProductVariantResponse.AttributeOption>> attributes = new LinkedHashMap<>();

        if (!variantResponses.isEmpty()) {
            // Xác định thứ tự attribute
            List<String> attrOrder = new ArrayList<>(variantResponses.get(0).getAttributes().keySet());
            String firstAttr = attrOrder.get(0);
            String lastAttr = attrOrder.get(attrOrder.size() - 1);

            // Build attribute đầu tiên (có image)
            Map<Object, String> valueToImage = new LinkedHashMap<>();
            for (VariantResponse vr : variantResponses) {
                Object val = vr.getAttributes().get(firstAttr);
                String image = vr.getImages().isEmpty() ? "" : vr.getImages().get(0);
                // Nếu đã có value rồi thì bỏ qua, giữ ảnh đầu tiên làm đại diện
                valueToImage.putIfAbsent(val, image);
            }

            List<ProductVariantResponse.AttributeOption> firstOptions = valueToImage.entrySet().stream()
                    .map(e -> ProductVariantResponse.AttributeOption.builder()
                            .value(e.getKey())
                            .image(e.getValue())
                            .build())
                    .collect(Collectors.toList());

            attributes.put(firstAttr, firstOptions);

            // Build các attribute giữa (không cần stock, không cần image)
            for (int i = 1; i < attrOrder.size() - 1; i++) {
                String attrName = attrOrder.get(i);
                Set<Object> vals = new LinkedHashSet<>();
                for (VariantResponse vr : variantResponses) {
                    vals.add(vr.getAttributes().get(attrName));
                }
                List<ProductVariantResponse.AttributeOption> options = vals.stream()
                        .map(v -> ProductVariantResponse.AttributeOption.builder()
                                .value(v)
                                .build())
                        .collect(Collectors.toList());
                attributes.put(attrName, options);
            }

            // Build attribute cuối (stock dựa trên các attribute khác = defaultVariant)
            String lastAttrName = lastAttr;
            Map<Object, Integer> stockMap = new LinkedHashMap<>();
            for (VariantResponse vr : variantResponses) {
                boolean match = true;
                for (int i = 0; i < attrOrder.size() - 1; i++) {
                    String attrName = attrOrder.get(i);
                    Object defaultVal = defaultVariant.getAttributes().get(attrName);
                    if (!vr.getAttributes().get(attrName).equals(defaultVal)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    Object lastVal = vr.getAttributes().get(lastAttrName);
                    stockMap.put(lastVal, stockMap.getOrDefault(lastVal, 0) + vr.getStock());
                }
            }
            List<ProductVariantResponse.AttributeOption> lastOptions = stockMap.entrySet().stream()
                    .map(e -> ProductVariantResponse.AttributeOption.builder()
                            .value(e.getKey())
                            .stock(e.getValue())
                            .build())
                    .collect(Collectors.toList());
            attributes.put(lastAttrName, lastOptions);
        }

        return ProductVariantResponse.builder()
                .attributes(attributes)
                .defaultVariant(defaultVariant)
                .build();
    }
    @Override
    public VariantSelectionResponse getVariantForSelection(String slug, Map<String, Object> selectedAttributes) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Variant> variants = product.getVariants();

        if (variants.isEmpty()) {
            return VariantSelectionResponse.builder().build();
        }

        // Thứ tự attribute
        List<String> attrOrder = new ArrayList<>(variants.get(0).getAttributes().keySet());
        String lastAttr = attrOrder.get(attrOrder.size() - 1);

        // Lọc các variant thỏa mãn selectedAttributes (ngoại trừ attribute cuối)
        List<VariantResponse> matchingVariants = variants.stream()
                .map(v -> {
                    Inventory inv = inventoryService.getInventoryByVariant(product.getId(), v.getId());
                    return VariantResponse.builder()
                            .id(v.getId())
                            .sku(v.getSku())
                            .attributes(v.getAttributes())
                            .images(v.getImages())
                            .displayOrder(v.getDisplayOrder())
                            .price(inv.getPrice())
                            .stock(inv.getAvailableStock())
                            .build();
                })
                .filter(vr -> {
                    for (Map.Entry<String, Object> entry : selectedAttributes.entrySet()) {
                        String attrName = entry.getKey();
                        Object val = entry.getValue();
                        if (!attrName.equals(lastAttr) && !val.equals(vr.getAttributes().get(attrName))) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // Tạo stock cho attribute cuối
        Map<Object, Integer> lastAttrStockMap = new LinkedHashMap<>();
        for (VariantResponse vr : matchingVariants) {
            Object val = vr.getAttributes().get(lastAttr);
            lastAttrStockMap.put(val, lastAttrStockMap.getOrDefault(val, 0) + vr.getStock());
        }
        List<VariantSelectionResponse.AttributeOption> lastAttrOptions = lastAttrStockMap.entrySet().stream()
                .map(e -> VariantSelectionResponse.AttributeOption.builder()
                        .value(e.getKey())
                        .stock(e.getValue())
                        .build())
                .collect(Collectors.toList());

        // Nếu user đã chọn đủ tất cả attribute → tìm variant chính xác
        VariantResponse selectedVariant = null;
        if (selectedAttributes.size() == attrOrder.size()) {
            for (VariantResponse vr : matchingVariants) {
                boolean match = true;
                for (String attr : attrOrder) {
                    if (!selectedAttributes.get(attr).equals(vr.getAttributes().get(attr))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    selectedVariant = vr;
                    break;
                }
            }
        }else{
            // Nếu chưa chọn đủ, lấy variant đầu tiên trong list matchingVariants (nếu có)
            if (!matchingVariants.isEmpty()) {
                selectedVariant = matchingVariants.get(0);
            }
        }

        return VariantSelectionResponse.builder()
                .lastAttributeName(lastAttr)
                .lastAttributeOptions(lastAttrOptions)
                .variant(selectedVariant)
                .build();
    }
}