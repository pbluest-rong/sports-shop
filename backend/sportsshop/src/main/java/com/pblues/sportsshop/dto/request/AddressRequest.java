package com.pblues.sportsshop.dto.request;

import com.pblues.sportsshop.model.Address;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
public class AddressRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private Integer provinceId;
    private String provinceName;
    private Integer districtId;
    private String districtName;
    private String wardCode;
    private String wardName;
    private String street;
    private String note;
    private String fullAddress;
    private BigDecimal lat;
    private BigDecimal lng;
}
