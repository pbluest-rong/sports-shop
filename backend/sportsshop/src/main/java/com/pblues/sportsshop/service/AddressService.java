package com.pblues.sportsshop.service;

import com.pblues.sportsshop.dto.request.AddressRequest;
import com.pblues.sportsshop.model.Address;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AddressService {
    Address addAddress(Authentication auth, AddressRequest address, boolean defaultAddress);

    Address updateAddress(Authentication auth, Long addressId, AddressRequest newAddress);

    void deleteAddress(Authentication auth, Long addressId);

    List<Address> getAddressesByUser(Authentication auth);

    void setDefaultAddress(Authentication auth, Long addressId);


}
