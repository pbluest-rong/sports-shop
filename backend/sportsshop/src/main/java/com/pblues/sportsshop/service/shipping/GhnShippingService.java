package com.pblues.sportsshop.service.shipping;

import com.pblues.sportsshop.client.GhnClient;
import com.pblues.sportsshop.dto.request.ShippingFeeRequest;
import com.pblues.sportsshop.dto.response.ShippingFeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GhnShippingService implements ShippingService {
    private final GhnClient ghnClient;
    @Value("${ghn.token}")
    private String token;
    @Value("${ghn.shopId}")
    private String shopId;

    @Override
    public ShippingFeeResponse.DataResponse calculateFee() {
        ShippingFeeRequest req = new ShippingFeeRequest();
        req.setFrom_district_id(1454);
        req.setFrom_ward_code("21211");
        req.setService_id(53320);
        req.setTo_district_id(1452);
        req.setTo_ward_code("21012");
        req.setHeight(50);
        req.setLength(20);
        req.setWeight(200);
        req.setWidth(20);
        req.setInsurance_value(10000);
        req.setCod_failed_amount(2000);

        ShippingFeeRequest.Item item = new ShippingFeeRequest.Item();
        item.setName("TEST1");
        item.setQuantity(1);
        item.setHeight(200);
        item.setWeight(1000);
        item.setLength(200);
        item.setWidth(200);

        req.setItems(List.of(item));

        return ghnClient.getShippingFee(
                token,
                shopId,
                req
        ).getData();
    }
}
