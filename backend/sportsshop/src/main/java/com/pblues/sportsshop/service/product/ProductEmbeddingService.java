package com.pblues.sportsshop.service.product;

import com.pblues.sportsshop.client.GeminiClient;
import com.pblues.sportsshop.dto.request.EmbedContentRequest;
import com.pblues.sportsshop.dto.request.EmbedListContentRequest;
import com.pblues.sportsshop.dto.response.EmbedContentResponse;
import com.pblues.sportsshop.dto.response.EmbedListContentResponse;
import com.pblues.sportsshop.dto.response.QdrantSearchResponse;
import com.pblues.sportsshop.model.Product;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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
    private final QdrantService qdrantService;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String MODEL = "embedding-001";

    /**
     * Tạo embedding cho 1 product và lưu vào Qdrant
     */
    public void generateAndSaveEmbedding(Product product) {
        String text = product.getTitle() + " " + product.getShortDescription();
        List<Double> embeddings = generateEmbedding(text);

        qdrantService.saveProductEmbedding(
                product.getId().toHexString(),
                embeddings,
                product.getTitle(),
                product.getShortDescription()
        );
    }

    /**
     * Tạo embedding cho list product và lưu vào Qdrant
     */
    public void generateAndSaveEmbedding(List<Product> products) {
        List<String> textList = products.stream()
                .map(p -> p.getTitle() + " " + p.getShortDescription())
                .collect(Collectors.toList());

        EmbedListContentResponse embedListContentResponse = generateEmbeddings(textList);

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            List<Double> vector = embedListContentResponse.getEmbeddings().get(i).getValues();

            qdrantService.saveProductEmbedding(
                    p.getId().toHexString(),
                    vector,
                    p.getTitle(),
                    p.getShortDescription()
            );
        }
    }

    /**
     * Gọi Gemini API để lấy embedding cho 1 text
     */
    public List<Double> generateEmbedding(String text) {
        EmbedContentRequest.Content.Part part = new EmbedContentRequest.Content.Part();
        part.setText(text);

        EmbedContentRequest.Content content = new EmbedContentRequest.Content();
        content.setParts(Collections.singletonList(part));

        EmbedContentRequest request = new EmbedContentRequest();
        request.setModel(MODEL);
        request.setContent(content);

        EmbedContentResponse response =
                geminiClient.embedContent(request, geminiApiKey);

        return response.getEmbedding().getValues();
    }

    /**
     * Gọi Gemini API để lấy embedding cho nhiều text
     */
    private EmbedListContentResponse generateEmbeddings(List<String> textList) {
        List<EmbedListContentRequest.Request> requests = new ArrayList<>();
        for (String text : textList) {
            EmbedListContentRequest.Request.Part part = new EmbedListContentRequest.Request.Part();
            part.setText(text);

            EmbedListContentRequest.Request.Content content = new EmbedListContentRequest.Request.Content();
            content.setParts(Collections.singletonList(part));

            EmbedListContentRequest.Request req = new EmbedListContentRequest.Request();
            req.setModel(MODEL);
            req.setContent(content);

            requests.add(req);
        }

        EmbedListContentRequest request = new EmbedListContentRequest();
        request.setRequests(requests);

        return geminiClient.embedContents(request, geminiApiKey);
    }

    /**
     * Search product theo text query
     */
    public List<QdrantSearchResponse.Result> searchProducts(String query, int limit) {
        List<Double> queryVector = generateEmbedding(query);
        return qdrantService.searchProducts(queryVector, limit);
    }

    public void removeEmbedding(ObjectId productId) {
        qdrantService.removeEmbedding(productId);
    }
}
