package com.pblues.sportsshop.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "product_embeddings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEmbedding {
    @Id
    private ObjectId productId;
    private List<Double> embeddings;
    private String model;
    private String textContent;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}