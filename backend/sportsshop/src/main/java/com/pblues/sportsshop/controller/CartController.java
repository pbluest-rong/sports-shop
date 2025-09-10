package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.dto.request.AddCartItemRequest;
import com.pblues.sportsshop.dto.response.ApiResponse;
import com.pblues.sportsshop.dto.response.CartResponse;
import com.pblues.sportsshop.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse> getCart(Authentication auth) {
        CartResponse cartResponse = cartService.getCart(auth);
        return ResponseEntity.ok().body(ApiResponse.success("Success", cartResponse));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addItem(Authentication auth, @RequestBody AddCartItemRequest request) {
        cartService.addItem(auth, request);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse> updateItem(
            Authentication auth,
            @PathVariable String productId,
            @RequestParam String sku,
            @RequestParam int quantity) {
        cartService.updateQuantity(auth, productId, sku, quantity);
        return ResponseEntity.ok(ApiResponse.success("success", null));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse> removeItem(Authentication auth, @PathVariable String productId, @RequestParam String sku) {
        cartService.removeItem(auth, productId, sku);
        return ResponseEntity.ok(ApiResponse.success("success", null));
    }
}

