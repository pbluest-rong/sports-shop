package com.pblues.sportsshop.service.payment.momo;

import com.pblues.sportsshop.dto.request.CreatePaymentRequest;
import com.pblues.sportsshop.dto.response.CreatePaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "momo", url = "${momo.end-point}")
public interface MomoApi {
    @PostMapping("/create")
    CreatePaymentResponse createMomoQR(@RequestBody CreatePaymentRequest createMomoRequest);
}
