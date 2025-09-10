package com.pblues.sportsshop.service.adress;

import com.pblues.sportsshop.dto.request.AddressRequest;
import com.pblues.sportsshop.model.Address;
import com.pblues.sportsshop.model.User;
import com.pblues.sportsshop.repository.AddressRepository;
import com.pblues.sportsshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public Address addAddress(Authentication auth, AddressRequest addressRequest, boolean defaultAddress) {
        Address address = mapperToAddress(addressRequest);

        User user = (User) auth.getPrincipal();
        address.setUser(user);
        address = addressRepository.save(address);
        if (defaultAddress) {
            user.setDefaultAddress(address);
            userRepository.save(user);
        }
        return address;
    }

    @Override
    public Address updateAddress(Authentication auth, Long addressId, AddressRequest newAddress) {
        User user = (User) auth.getPrincipal();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!(address.getUser().getId() == user.getId())) {
            throw new RuntimeException("Forbidden: You cannot update this address");
        }

        address.setFirstName(newAddress.getFirstName());
        address.setLastName(newAddress.getLastName());
        address.setPhone(newAddress.getPhone());
        address.setProvinceId(newAddress.getProvinceId());
        address.setProvinceName(newAddress.getProvinceName());
        address.setDistrictId(newAddress.getDistrictId());
        address.setDistrictName(newAddress.getDistrictName());
        address.setWardCode(newAddress.getWardCode());
        address.setWardName(newAddress.getWardName());
        address.setStreet(newAddress.getStreet());
        address.setNote(newAddress.getNote());
        address.setFullAddress(newAddress.getFullAddress());
        address.setLat(newAddress.getLat());
        address.setLng(newAddress.getLng());

        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Authentication auth, Long addressId) {
        User user = (User) auth.getPrincipal();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!(address.getUser().getId() == user.getId())) {
            throw new RuntimeException("Forbidden: You cannot delete this address");
        }

        addressRepository.delete(address);
    }

    @Override
    public List<Address> getAddressesByUser(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return addressRepository.findByUserId(user.getId());
    }

    @Override
    public void setDefaultAddress(Authentication auth, Long addressId) {
        User user = (User) auth.getPrincipal();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (address.getUser().getId() == user.getId()) {
            user.setDefaultAddress(address);
            userRepository.save(user);
        }
    }

    private Address mapperToAddress(AddressRequest addressRequest) {
        Address address = new Address();
        address.setFirstName(addressRequest.getFirstName());
        address.setLastName(addressRequest.getLastName());
        address.setPhone(addressRequest.getPhone());
        address.setProvinceId(addressRequest.getProvinceId());
        address.setProvinceName(addressRequest.getProvinceName());
        address.setDistrictId(addressRequest.getDistrictId());
        address.setDistrictName(addressRequest.getDistrictName());
        address.setWardCode(addressRequest.getWardCode());
        address.setWardName(addressRequest.getWardName());
        address.setStreet(addressRequest.getStreet());
        address.setNote(addressRequest.getNote());
        address.setFullAddress(addressRequest.getFullAddress());
        address.setLat(addressRequest.getLat());
        address.setLng(addressRequest.getLng());
        return address;
    }
}
