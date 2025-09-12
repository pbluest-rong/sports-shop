package com.pblues.sportsshop.dto.response;

import lombok.Data;

@Data
public class BreadcrumbResponse {
    private String name;
    private String path;

    public BreadcrumbResponse(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
