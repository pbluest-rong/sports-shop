package com.pblues.sportsshop.client;

import com.pblues.sportsshop.dto.request.DistrictRequest;
import com.pblues.sportsshop.dto.request.ShippingFeeRequest;
import com.pblues.sportsshop.dto.request.WardRequest;
import com.pblues.sportsshop.dto.response.DistrictListResponse;
import com.pblues.sportsshop.dto.response.ProvinceListResponse;
import com.pblues.sportsshop.dto.response.ShippingFeeResponse;
import com.pblues.sportsshop.dto.response.WardListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "giaohangnhanh",
        url = "https://online-gateway.ghn.vn/shiip/public-api"
)
public interface GhnClient {

    @PostMapping("/v2/shipping-order")
    ShippingFeeResponse getShippingFee(
            @RequestHeader("token") String token,
            @RequestHeader("ShopId") String shopId,
            @RequestBody ShippingFeeRequest request
    );

    @PostMapping("/master-data/province")
    ProvinceListResponse getProvinces(
            @RequestHeader("token") String token
    );

    @PostMapping("/master-data/district")
    DistrictListResponse getDistricts(
            @RequestHeader("token") String token,
            @RequestBody DistrictRequest request
    );

    @PostMapping("/master-data/ward?district_id")
    WardListResponse getWards(
            @RequestHeader("token") String token,
            @RequestBody WardRequest request
    );
}