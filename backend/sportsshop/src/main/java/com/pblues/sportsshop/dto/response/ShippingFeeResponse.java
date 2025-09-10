package com.pblues.sportsshop.dto.response;

import lombok.Data;

@Data
public class ShippingFeeResponse {
    private Integer code;
    private String message;
    private DataResponse data;

    @Data
    public static class DataResponse {
        private Integer total;
        private Integer service_fee;
        private Integer insurance_fee;
        private Integer pick_station_fee;
        private Integer coupon_value;
        private Integer r2s_fee;
        private Integer return_again;
        private Integer document_return;
        private Integer double_check;
        private Integer cod_fee;
        private Integer pick_remote_areas_fee;
        private Integer deliver_remote_areas_fee;
        private Integer cod_failed_fee;
    }
}
