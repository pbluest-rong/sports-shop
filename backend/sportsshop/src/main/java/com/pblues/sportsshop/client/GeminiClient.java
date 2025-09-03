package com.pblues.sportsshop.client;

import com.pblues.sportsshop.dto.request.EmbedContentRequest;
import com.pblues.sportsshop.dto.request.EmbedListContentRequest;
import com.pblues.sportsshop.dto.response.EmbedContentResponse;
import com.pblues.sportsshop.dto.response.EmbedListContentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "gemini", url = "https://generativelanguage.googleapis.com/v1beta")
public interface GeminiClient {

    @PostMapping("/models/embedding-001:embedContent")
    EmbedContentResponse embedContent(@RequestBody EmbedContentRequest request,
                                      @RequestHeader("x-goog-api-key") String apiKey,
                                      @RequestHeader("Content-Type") String contentType);

    @PostMapping("/models/gemini-embedding-001:batchEmbedContents")
    EmbedListContentResponse embedContents(@RequestBody EmbedListContentRequest request,
                                           @RequestHeader("x-goog-api-key") String apiKey,
                                           @RequestHeader("Content-Type") String contentType);
}