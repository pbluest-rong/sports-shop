package com.pblues.sportsshop.client;

import com.pblues.sportsshop.common.config.QdrantFeignConfig;
import com.pblues.sportsshop.dto.request.QdrantCreateCollectionRequest;
import com.pblues.sportsshop.dto.request.QdrantDeleteRequest;
import com.pblues.sportsshop.dto.request.QdrantSearchRequest;
import com.pblues.sportsshop.dto.request.QdrantUpsertRequest;
import com.pblues.sportsshop.dto.response.QdrantSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(
        name = "qdrantClient",
        url = "${qdrant.url}",
        configuration = QdrantFeignConfig.class
)
public interface QdrantClient {
    @PutMapping("/collections/{collectionName}")
    void createCollection(
            @PathVariable("collectionName") String collectionName,
            @RequestBody QdrantCreateCollectionRequest request
    );

    @PostMapping("/collections/{collectionName}/points")
    void upsertPoints(
            @PathVariable("collectionName") String collectionName,
            @RequestBody QdrantUpsertRequest request
    );

    @PostMapping("/collections/{collectionName}/points/search")
    QdrantSearchResponse searchPoints(
            @PathVariable("collectionName") String collectionName,
            @RequestBody QdrantSearchRequest request
    );

    @PostMapping("/collections/{collectionName}/points/delete")
    void deletePoints(
            @PathVariable("collectionName") String collectionName,
            @RequestBody QdrantDeleteRequest request
    );
}
