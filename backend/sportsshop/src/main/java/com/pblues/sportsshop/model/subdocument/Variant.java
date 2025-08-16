package com.pblues.sportsshop.model.subdocument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variant {
    private String sku;
    private Map<String, Object> attrs; // {"size": 40, "color": "red"}
    private long price;
    private int stock;
}
