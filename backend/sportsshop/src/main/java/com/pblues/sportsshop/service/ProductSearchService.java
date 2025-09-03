package com.pblues.sportsshop.service;

import com.pblues.sportsshop.model.Product;
import com.pblues.sportsshop.repository.ProductRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
public class ProductSearchService {
    private final MongoTemplate mongoTemplate;
    private final ProductEmbeddingService embeddingService;
    private final ProductRepository productRepository;



    public Page<Product> vectorSearch(String queryText, int page, int size) {
        // Validate pagination parameters
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page must be non-negative and size must be positive");
        }

        // Generate embedding for the query text
        List<Double> queryEmbedding;
        try {
            queryEmbedding = embeddingService.generateEmbedding(queryText);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding: " + e.getMessage(), e);
        }

        // Calculate skip value for pagination
        int skip = page * size;
        int numCandidates = size * 10; // Adjust numCandidates for accuracy

        // Create custom $vectorSearch aggregation stage
        Document vectorSearchStage = new Document("$vectorSearch",
                new Document("index", "product_embedding_index")
                        .append("path", "embeddings")
                        .append("queryVector", queryEmbedding)
                        .append("numCandidates", numCandidates)
                        .append("limit", size * 2) // Fetch extra results for filtering
        );

        // Create project stage to include productId and score
        Document projectStage = new Document("$project",
                new Document("productId", 1)
                        .append("score", new Document("$meta", "vectorSearchScore"))
                        .append("_id", 0)
        );

        // Create skip and limit stages for pagination
        Document skipStage = new Document("$skip", skip);
        Document limitStage = new Document("$limit", size);

        // Count total results for pagination metadata
        Document countStage = new Document("$count", "total");
        Aggregation countAggregation = Aggregation.newAggregation(
                context -> vectorSearchStage,
                context -> countStage
        );
        Document countResult = mongoTemplate.aggregate(countAggregation, "product_embeddings", Document.class)
                .getMappedResults()
                .stream()
                .findFirst()
                .orElse(new Document("total", 0));
        long total = countResult.getInteger("total", 0);

        // Build main aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                context -> vectorSearchStage,
                context -> projectStage,
                context -> skipStage,
                context -> limitStage
        );

        // Execute aggregation
        List<ProductEmbeddingWithScore> results = mongoTemplate
                .aggregate(aggregation, "product_embeddings", ProductEmbeddingWithScore.class)
                .getMappedResults();

        // Extract productIds and preserve order with scores
        List<ObjectId> productIds = results.stream()
                .map(ProductEmbeddingWithScore::getProductId)
                .collect(Collectors.toList());

        // Map scores for potential use
        Map<ObjectId, Double> scoreMap = results.stream()
                .collect(Collectors.toMap(ProductEmbeddingWithScore::getProductId, ProductEmbeddingWithScore::getScore));

        // Fetch products, preserving order
        List<Product> products = productRepository.findAllById(productIds)
                .stream()
                .filter(product -> productIds.contains(product.getId())) // Ensure only requested IDs are included
                .sorted((p1, p2) -> {
                    // Sort by score to maintain vector search relevance
                    Double score1 = scoreMap.getOrDefault(p1.getId(), 0.0);
                    Double score2 = scoreMap.getOrDefault(p2.getId(), 0.0);
                    return score2.compareTo(score1); // Descending order
                })
                .collect(Collectors.toList());

        // Return Page object with pagination metadata
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(products, pageable, total);
    }

    @Data
    public static class ProductEmbeddingWithScore {
        private ObjectId productId;
        private double score;
    }
}