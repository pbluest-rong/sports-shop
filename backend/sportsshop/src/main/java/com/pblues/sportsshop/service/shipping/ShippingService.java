package com.pblues.sportsshop.service.shipping;

import com.pblues.sportsshop.dto.response.ShippingFeeResponse;

public interface ShippingService {
    ShippingFeeResponse.DataResponse calculateFee();
}
