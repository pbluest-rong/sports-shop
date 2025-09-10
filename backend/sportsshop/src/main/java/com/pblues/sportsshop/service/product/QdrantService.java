package com.pblues.sportsshop.service.product;

import com.pblues.sportsshop.client.QdrantClient;
import com.pblues.sportsshop.dto.request.QdrantCreateCollectionRequest;
import com.pblues.sportsshop.dto.request.QdrantDeleteRequest;
import com.pblues.sportsshop.dto.request.QdrantSearchRequest;
import com.pblues.sportsshop.dto.request.QdrantUpsertRequest;
import com.pblues.sportsshop.dto.response.QdrantSearchResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QdrantService {
    private final QdrantClient qdrantClient;

    @Value("${qdrant.collection}")
    private String collectionName;

    public void createCollectionIfNotExists() {
        QdrantCreateCollectionRequest request = new QdrantCreateCollectionRequest(
                new QdrantCreateCollectionRequest.VectorsConfig(768, "Cosine")
        );
        qdrantClient.createCollection(collectionName, request);
    }

    public void saveProductEmbedding(String productId, List<Double> vector, String title, String description) {
        QdrantUpsertRequest.Point point = new QdrantUpsertRequest.Point(
                productId,
                vector,
                Map.of(
                        "title", title,
                        "description", description
                )
        );
        QdrantUpsertRequest request = new QdrantUpsertRequest(List.of(point));
        qdrantClient.upsertPoints(collectionName, request);
    }

    public List<QdrantSearchResponse.Result> searchProducts(List<Double> queryVector, int limit) {
        QdrantSearchRequest request = new QdrantSearchRequest(queryVector, limit);
        QdrantSearchResponse response = qdrantClient.searchPoints(collectionName, request);
        return response.getResult();
    }

    public void removeEmbedding(ObjectId productId) {
        String id = productId.toHexString();
        QdrantDeleteRequest request = new QdrantDeleteRequest(
                new QdrantDeleteRequest.DeletePoints(List.of(id))
        );
        qdrantClient.deletePoints(collectionName, request);
    }
}
