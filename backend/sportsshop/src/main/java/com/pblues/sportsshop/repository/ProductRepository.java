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
}
