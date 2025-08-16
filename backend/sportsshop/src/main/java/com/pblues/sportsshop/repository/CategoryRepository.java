package com.pblues.sportsshop.repository;

import com.pblues.sportsshop.model.Category;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, ObjectId> {
   Optional<Category> findBySlug(String slug);
}
