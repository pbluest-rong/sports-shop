package com.pblues.sportsshop.kafka.product;

import com.pblues.sportsshop.common.exception.ResourceNotFoundException;
import com.pblues.sportsshop.model.Inventory;
import com.pblues.sportsshop.model.Product;
import com.pblues.sportsshop.model.subdocument.Variant;
import com.pblues.sportsshop.repository.ProductRepository;
import com.pblues.sportsshop.service.product.ProductEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductEventHandler {
    private final ProductRepository productRepository;
    private final ProductEmbeddingService embeddingService;

    public void handle(ProductEvent event) {
        switch (event.getAction()) {
            case CREATE -> handleCreate(event.getProductId());
            case UPDATE -> handleUpdate(event.getProductId());
            case DELETE -> handleDelete(event.getProductId());
        }
    }

    private void handleCreate(String productId) {
        Product product = productRepository.findById(new ObjectId(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        embeddingService.generateAndSaveEmbedding(product);
    }

    private void handleUpdate(String productId) {
        Product product = productRepository.findById(new ObjectId(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        embeddingService.generateAndSaveEmbedding(product);
    }

    private void handleDelete(String productId) {
        embeddingService.removeEmbedding(new ObjectId(productId));
    }
}