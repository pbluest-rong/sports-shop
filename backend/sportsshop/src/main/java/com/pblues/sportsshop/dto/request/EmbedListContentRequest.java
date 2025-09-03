package com.pblues.sportsshop.dto.request;

import lombok.Data;

import java.util.List;
import lombok.Data;
import java.util.List;

@Data
public class EmbedListContentRequest {
    private List<Request> requests;

    @Data
    public static class Request {
        private String model;
        private Content content;

        @Data
        public static class Content {
            private List<Part> parts;
        }

        @Data
        public static class Part {
            private String text;
        }
    }
}
