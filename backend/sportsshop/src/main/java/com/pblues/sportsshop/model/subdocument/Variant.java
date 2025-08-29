package com.pblues.sportsshop.model.subdocument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variant {
    @Field("id")
    private String id;
    private String sku;
    private Map<String, Object> attributes;
    @Field("images")
    private List<String> images;
    private int displayOrder;
}
