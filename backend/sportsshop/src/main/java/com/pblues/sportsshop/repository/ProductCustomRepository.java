package com.pblues.sportsshop.repository;

import com.pblues.sportsshop.model.Product;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ProductCustomRepository {
    private final MongoTemplate mongoTemplate;

    public ProductCustomRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<Product> findProductsWithFilters(
            String queryStr,
            ObjectId categoryId,
            String brand,
            Long priceMin,
            Long priceMax,
            String productSize,
            String color,
            Pageable pageable
    ) {
        Query query = new Query().with(pageable);

        if (queryStr != null && !queryStr.isEmpty()) {
            query.addCriteria(new Criteria().orOperator(
                Criteria.where("name").regex(queryStr, "i"),
                Criteria.where("brand").regex(queryStr, "i"),
                Criteria.where("description").regex(queryStr, "i")
            ));
        }

        if (categoryId != null) {
            query.addCriteria(Criteria.where("categoryId").is(categoryId));
        }

        if (brand != null && !brand.isEmpty()) {
            query.addCriteria(Criteria.where("brand").regex(brand, "i"));
        }

        if (priceMin != null || priceMax != null) {
            Criteria priceCriteria = new Criteria();
            if (priceMin != null) priceCriteria = priceCriteria.gte(priceMin);
            if (priceMax != null) priceCriteria = priceCriteria.lte(priceMax);
            query.addCriteria(Criteria.where("variants.price").elemMatch(priceCriteria));
        }

        if (productSize != null) {
            query.addCriteria(Criteria.where("variants.attrs.size").is(productSize));
        }

        if (color != null) {
            query.addCriteria(Criteria.where("variants.attrs.color").regex(color, "i"));
        }

        long total = mongoTemplate.count(query, Product.class);
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Product.class),
                pageable,
                () -> total
        );
    }
}
