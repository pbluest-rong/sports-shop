package com.pblues.sportsshop.client;

import com.pblues.sportsshop.dto.request.EmbedContentRequest;
import com.pblues.sportsshop.dto.request.EmbedListContentRequest;
import com.pblues.sportsshop.dto.response.EmbedContentResponse;
import com.pblues.sportsshop.dto.response.EmbedListContentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "gemini", url = "https://generativelanguage.googleapis.com/v1beta")
public interface GeminiClient {

    @PostMapping(value = "/models/embedding-001:embedContent",
            consumes = "application/json")
    EmbedContentResponse embedContent(@RequestBody EmbedContentRequest request,
                                      @RequestHeader("x-goog-api-key") String apiKey);

    @PostMapping(value = "/models/gemini-embedding-001:batchEmbedContents",
            consumes = "application/json")
    EmbedListContentResponse embedContents(@RequestBody EmbedListContentRequest request,
                                           @RequestHeader("x-goog-api-key") String apiKey);
}
