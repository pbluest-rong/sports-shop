package com.pblues.sportsshop.service;

import com.pblues.sportsshop.client.GeminiClient;
import com.pblues.sportsshop.dto.request.EmbedContentRequest;
import com.pblues.sportsshop.dto.request.EmbedListContentRequest;
import com.pblues.sportsshop.dto.response.EmbedContentResponse;
import com.pblues.sportsshop.dto.response.EmbedListContentResponse;
import com.pblues.sportsshop.model.Product;
import com.pblues.sportsshop.model.ProductEmbedding;
import com.pblues.sportsshop.repository.ProductEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductEmbeddingService {
    private final GeminiClient geminiClient;
    private final ProductEmbeddingRepository productEmbeddingRepository;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String MODEL = "embedding-001";

    public void generateAndSaveEmbedding(Product product) {
        String text = product.getTitle() + " " + product.getDescription();
        List<Double> embeddings = generateEmbedding(text);

        ProductEmbedding embeddingEntity = ProductEmbedding.builder()
                .productId(product.getId())
                .embeddings(embeddings)
                .model(MODEL)
                .textContent(text)
                .build();
        productEmbeddingRepository.save(embeddingEntity);
    }

    public void generateAndSaveEmbedding(List<Product> products) {
        List<String> textList = products.stream().map(p -> p.getTitle() + " " + p.getDescription()).collect(Collectors.toList());
        EmbedListContentResponse embedListContentResponse = generateEmbeddings(textList);

        for(int index =0; index < products.size(); index++) {
            EmbedListContentResponse.Embedding embed = embedListContentResponse.getEmbeddings().get(index);
            Product p = products.get(index);
            ProductEmbedding embeddingEntity = ProductEmbedding.builder()
                    .productId(p.getId())
                    .embeddings(embed.getValues())
                    .model(MODEL)
                    .textContent(p.getTitle() + " " + p.getDescription())
                    .build();
            productEmbeddingRepository.save(embeddingEntity);
        }
    }

    public List<Double> generateEmbedding(String text) {
        EmbedContentRequest.Content.Part part = new EmbedContentRequest.Content.Part();
        part.setText(text);

        EmbedContentRequest.Content content = new EmbedContentRequest.Content();
        content.setParts(Collections.singletonList(part));

        EmbedContentRequest request = new EmbedContentRequest();
        request.setModel(MODEL);
        request.setContent(content);

        EmbedContentResponse response = geminiClient.embedContent(request, geminiApiKey, "application/json");
        return response.getEmbedding().getValues();
    }

    private EmbedListContentResponse generateEmbeddings(List<String> textList) {
        List<EmbedListContentRequest.Request> requests = new ArrayList<>();
        for (String text : textList) {
            EmbedListContentRequest.Request.Part part = new EmbedListContentRequest.Request.Part();
            part.setText(text);
            List<EmbedListContentRequest.Request.Part> parts = Collections.singletonList(part);

            EmbedListContentRequest.Request.Content content = new EmbedListContentRequest.Request.Content();
            content.setParts(parts);


            EmbedListContentRequest.Request request = new EmbedListContentRequest.Request();
            request.setModel(MODEL);
            request.setContent(content);
            requests.add(request);
        }
        EmbedListContentRequest request = new EmbedListContentRequest();
        request.setRequests(requests);

        EmbedListContentResponse response = geminiClient.embedContents(request, geminiApiKey, "application/json");
        return response;
    }
}