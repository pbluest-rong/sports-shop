package com.pblues.sportsshop.service;

import com.pblues.sportsshop.model.Address;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AddressService {
    Address addAddress(Authentication auth, Address address, boolean defaultAddress);

    Address updateAddress(Authentication auth, Long addressId, Address newAddress);

    void deleteAddress(Authentication auth, Long addressId);

    List<Address> getAddressesByUser(Authentication auth);

    void setDefaultAddress(Authentication auth, Long addressId);
}
