package com.pblues.sportsshop.repository;

import com.pblues.sportsshop.model.ProductEmbedding;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEmbeddingRepository extends MongoRepository<ProductEmbedding, ObjectId> {
}
