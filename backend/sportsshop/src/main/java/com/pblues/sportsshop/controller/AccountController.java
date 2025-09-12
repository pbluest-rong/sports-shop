package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.dto.request.AddressRequest;
import com.pblues.sportsshop.model.Address;
import com.pblues.sportsshop.dto.response.ApiResponse;
import com.pblues.sportsshop.service.adress.AddressService;
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

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse> addAddress(Authentication auth,
                                                  @RequestBody AddressRequest address,
                                                  @RequestParam(defaultValue = "false", required = false) boolean defaultAddress) {
        Address data = addressService.addAddress(auth, address, defaultAddress);
        return ResponseEntity.ok()
                .body(ApiResponse.success("Successfully added address", data));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse> updateAddress(Authentication auth,
                                                     @PathVariable Long addressId,
                                                     @RequestBody AddressRequest address) {
        Address data = addressService.updateAddress(auth, addressId, address);
        return ResponseEntity.ok()
                .body(ApiResponse.success("Successfully updated address", data));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(Authentication auth,
                                                     @PathVariable Long addressId) {
        addressService.deleteAddress(auth, addressId);
        return ResponseEntity.ok()
                .body(ApiResponse.success("Successfully deleted address", addressId));
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse> getMyAddresses(Authentication auth) {
        List<Address> data = addressService.getAddressesByUser(auth);
        return ResponseEntity.ok()
                .body(ApiResponse.success("Successfully retrieved addresses", data));
    }

    @PatchMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(Authentication auth, Long addressId) {
        addressService.setDefaultAddress(auth, addressId);
        return ResponseEntity.ok(ApiResponse.success("success", null));
    }
}