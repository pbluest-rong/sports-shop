package com.pblues.sportsshop.service;

import com.pblues.sportsshop.dto.request.AddCartItemRequest;
import com.pblues.sportsshop.dto.response.CartResponse;
import com.pblues.sportsshop.model.subdocument.CartItem;
import org.springframework.security.core.Authentication;

import java.util.Set;

public interface CartService {
    void addItem(Authentication auth, AddCartItemRequest request);

    CartResponse getCart(Authentication auth);

    void updateQuantity(Authentication auth, String productId, String sku, int quantity);

    void removeItem(Authentication auth, String productId, String sku);

    void removeItems(Authentication auth, Set<CartItem> itemsToRemove);
}