package com.pblues.sportsshop.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "categories")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Category {
    @Id
    private ObjectId id;

    private String name;
    private String slug;
    private ObjectId parentId;
    private String icon;

    @CreatedDate
    private LocalDateTime createdAt;
}
