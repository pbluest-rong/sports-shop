package com.pblues.sportsshop.repository;

import com.pblues.sportsshop.model.Category;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, ObjectId> {
    boolean existsByParentIdAndIsActiveTrue(ObjectId parentId);

    Optional<Category> findBySlugAndIsActiveTrue(String slug);

    Optional<Category> findByPathAndIsActiveTrue(String path);

    Optional<Category> findByIdAndIsActiveTrue(ObjectId id);

    List<Category> findByParentId(ObjectId parentId);

    List<Category> findByParentIdAndIsActiveTrue(ObjectId parentId);

    @Query(value = "{ 'path': { $regex: '^?0(/.*)?' }, 'isActive': true }")
    List<Category> findActiveDescendants(String path);
}
