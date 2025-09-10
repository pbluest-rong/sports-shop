package com.pblues.sportsshop.model;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "categories")
@NoArgsConstructor
@Data
public class Category {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String name;
    @Indexed(unique = true)
    private String slug;
    private String imageUrl;
    private Integer level = 1;
    private Integer order = 1;

    @Field("parentId")
    private ObjectId parentId;
    private String path;
    private Boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    // root
    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
        this.level = 1;
        this.parentId = null;
        this.path = null;
    }

    // child
    public Category(String name, String slug, ObjectId parentId) {
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
    }
}
