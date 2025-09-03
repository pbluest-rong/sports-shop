package com.pblues.sportsshop.service;

import com.pblues.sportsshop.exception.ResourceNotFoundException;
import com.pblues.sportsshop.model.Cart;
import com.pblues.sportsshop.model.Inventory;
import com.pblues.sportsshop.model.subdocument.CartItem;
import com.pblues.sportsshop.model.Product;
import com.pblues.sportsshop.model.User;
import com.pblues.sportsshop.model.subdocument.Variant;
import com.pblues.sportsshop.repository.CartRepository;
import com.pblues.sportsshop.repository.ProductRepository;
import com.pblues.sportsshop.dto.request.AddCartItemRequest;
import com.pblues.sportsshop.dto.response.CartItemResponse;
import com.pblues.sportsshop.dto.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final InventoryService inventoryService;

    @Override
    public void addItem(Authentication auth, AddCartItemRequest request) {
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User user = (User) auth.getPrincipal();

        Product product = productRepository.findById(new ObjectId(request.getProductId()))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Variant variant = product.getVariants().stream().filter(v -> v.getSku().equals(request.getSku())).findFirst().orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        Inventory inventory = inventoryService.getInventoryByVariant(product.getId(), variant.getId());

        // Tìm cart của user, nếu chưa có thì tạo mới
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart c = new Cart();
            c.setUserId(user.getId());
            return c;
        });

        int quantity = request.getQuantity();

        // Kiểm tra item đã tồn tại chưa
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()) && i.getSku().equals(request.getSku()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int itemQuantity = existingItem.getQuantity() + quantity;
            if (itemQuantity > inventory.getAvailableStock())
                throw new ResourceNotFoundException("Quantity exceeds available stock");
            existingItem.setQuantity(itemQuantity);
            cart.getItems().add(existingItem);
        } else {
            if (quantity > inventory.getQuantity())
                throw new ResourceNotFoundException("Quantity exceeds available stock");

            CartItem cartItem = new CartItem();
            cartItem.setProductId(request.getProductId());
            cartItem.setSku(request.getSku());
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }
        cartRepository.save(cart);
    }

    @Override
    public CartResponse getCart(Authentication auth) {
        User user = (User) auth.getPrincipal();

        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Set<CartItemResponse> items = cart.getItems().stream().map(item -> {
            Product product = productRepository.findById(new ObjectId(item.getProductId())).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            Variant variant = product.getVariants().stream().filter(v -> v.getSku().equals(item.getSku())).findFirst().orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

            Inventory inventory = inventoryService.getInventoryByVariant(product.getId(), variant.getId());

            return CartItemResponse.builder()
                    .id(item.getId())
                    .quantity(item.getQuantity())
                    .unitPrice(inventory.getPrice())
                    .totalPrice(inventory.getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .productId(item.getProductId())
                    .sku(item.getSku())
                    .imageUrl(variant.getImages().isEmpty()?null:variant.getImages().get(0))
                    .variant(variant).build();
        }).collect(Collectors.toSet());

        CartResponse cartResponse = new CartResponse();
        cartResponse.setItems(items);
        return cartResponse;
    }

    @Override
    public void updateQuantity(Authentication auth, String productId, String sku, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User user = (User) auth.getPrincipal();

        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream().filter(i -> i.getProductId().equals(productId) && i.getSku().equals(sku)).findFirst().orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        item.setQuantity(quantity);

        cartRepository.save(cart);
    }

    @Override
    public void removeItem(Authentication auth, String productId, String sku) {
        User user = (User) auth.getPrincipal();

        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        boolean removed = cart.getItems().removeIf(i -> i.getProductId().equals(productId) && i.getSku().equals(sku));

        if (!removed) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        cartRepository.save(cart);
    }

    @Override
    public void removeItems(Authentication auth, Set<CartItem> itemsToRemove) {
        User user = (User) auth.getPrincipal();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        boolean removed = cart.getItems().removeIf(item ->
                itemsToRemove.stream().anyMatch(toRemove ->
                        toRemove.getProductId().equals(item.getProductId()) &&
                                toRemove.getSku().equals(item.getSku())
                )
        );

        if (!removed) {
            throw new ResourceNotFoundException("No matching cart items found");
        }

        cartRepository.save(cart);
    }
}