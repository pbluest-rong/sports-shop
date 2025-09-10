package com.pblues.sportsshop.repository;

import com.pblues.sportsshop.model.Product;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, ObjectId> {

    Page<Product> findByCategoryId(ObjectId categoryId, Pageable pageable);

    Optional<Product> findBySlug(String slug);

    @Query("{ 'ancestorIds': ?0, 'status': 'ACTIVE' }")
    Page<Product> findActiveProductsByAncestorId(ObjectId categoryId, Pageable pageable);

    @Query("{ '$or': [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ], 'ancestorIds': ?1, 'status': 'ACTIVE' }")
    Page<Product> searchByNameAndCategory(String searchTerm, ObjectId categoryId, Pageable pageable);

    @Query("{ 'status': 'ACTIVE', 'ancestorIds': { $in: ?0 } }")
    Page<Product> findActiveProductsByMultipleCategories(List<ObjectId> categoryIds, Pageable pageable);
}
