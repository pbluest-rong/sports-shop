package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.dto.request.AddressRequest;
import com.pblues.sportsshop.model.Address;
import com.pblues.sportsshop.dto.response.ApiResponse;
import com.pblues.sportsshop.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AddressService addressService;

    @PostMapping("addresses")
    public ResponseEntity<Address> addAddress(Authentication auth,
                                              @RequestBody AddressRequest address,
                                              @RequestParam(defaultValue = "false", required = false) boolean defaultAddress) {
        return ResponseEntity.ok(addressService.addAddress(auth, address, defaultAddress));
    }

    @PutMapping("addresses/{addressId}")
    public ResponseEntity<Address> updateAddress(Authentication auth,
                                                 @PathVariable Long addressId,
                                                 @RequestBody AddressRequest address) {
        return ResponseEntity.ok(addressService.updateAddress(auth, addressId, address));
    }

    @DeleteMapping("addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(Authentication auth,
                                              @PathVariable Long addressId) {
        addressService.deleteAddress(auth, addressId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("addresses")
    public ResponseEntity<List<Address>> getMyAddresses(Authentication auth) {
        return ResponseEntity.ok(addressService.getAddressesByUser(auth));
    }

    @PatchMapping("addresses/{addressId}")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(Authentication auth, Long addressId) {
        addressService.setDefaultAddress(auth, addressId);
        return ResponseEntity.ok(ApiResponse.success("success", null));
    }
}
