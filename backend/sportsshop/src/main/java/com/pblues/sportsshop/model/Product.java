package com.pblues.sportsshop.model;

import com.pblues.sportsshop.model.subdocument.Rating;
import com.pblues.sportsshop.model.subdocument.Variant;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Product {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String slug;
    private String title;
    private String description;
    private String brand;
    private ProductStatus status;
    private int sold;
    private ObjectId categoryId;

    private Map<String, List<String>> attributes;

    private List<Variant> variants;

    private Rating rating;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum ProductStatus {
        ACTIVE,
        INACTIVE,
        DISCONTINUED,
    }
}
