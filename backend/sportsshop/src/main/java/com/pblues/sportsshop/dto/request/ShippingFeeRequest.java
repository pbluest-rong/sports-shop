package com.pblues.sportsshop.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ShippingFeeRequest {
    private Integer from_district_id;
    private String from_ward_code;
    private Integer service_id;
    private Integer service_type_id;
    private Integer to_district_id;
    private String to_ward_code;
    private Integer height;
    private Integer length;
    private Integer weight;
    private Integer width;
    private Integer insurance_value;
    private Integer cod_failed_amount;
    private String coupon;
    private List<Item> items;

    @Data
    public static class Item {
        private String name;
        private Integer quantity;
        private Integer height;
        private Integer weight;
        private Integer length;
        private Integer width;
    }
}
