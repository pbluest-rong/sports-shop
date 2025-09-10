package com.pblues.sportsshop.service.order;

import com.pblues.sportsshop.common.constant.OrderStatus;
import com.pblues.sportsshop.common.constant.PaymentMethod;
import com.pblues.sportsshop.common.exception.PriceChangedException;
import com.pblues.sportsshop.common.exception.ResourceNotFoundException;
import com.pblues.sportsshop.model.*;
import com.pblues.sportsshop.model.OrderItem;
import com.pblues.sportsshop.model.subdocument.CartItem;
import com.pblues.sportsshop.model.subdocument.Variant;
import com.pblues.sportsshop.repository.AddressRepository;
import com.pblues.sportsshop.repository.CartRepository;
import com.pblues.sportsshop.repository.OrderRepository;
import com.pblues.sportsshop.repository.ProductRepository;
import com.pblues.sportsshop.dto.request.OrderRequest;
import com.pblues.sportsshop.dto.response.OrderResponse;
import com.pblues.sportsshop.dto.response.CreatePaymentResponse;
import com.pblues.sportsshop.dto.response.OrderCreationResponse;
import com.pblues.sportsshop.service.inventory.InventoryService;
import com.pblues.sportsshop.service.cart.CartService;
import com.pblues.sportsshop.service.payment.PaymentService;
import com.pblues.sportsshop.common.util.OrderUtils;
import com.pblues.sportsshop.service.shipping.ShippingService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final ShippingService shippingService;

    @Override
    @Transactional
    public OrderCreationResponse checkout(Authentication auth, OrderRequest request) {
        User user = (User) auth.getPrincipal();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        String orderId = OrderUtils.generateId();
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Inventory> inventories = new ArrayList<>();
        OrderStatus status = request.getPaymentMethod() == PaymentMethod.COD ?
                OrderStatus.PENDING :
                OrderStatus.AWAITING_PAYMENT;

        Address address = addressRepository.findById(request.getAddressId()).orElseThrow(
                () -> new ResourceNotFoundException("Address not found")
        );

        BigDecimal deliveryFee = new BigDecimal(shippingService.calculateFee().getTotal());

        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .orderDateTime(LocalDateTime.now())
                .fullAddress(address.getFullAddress())
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .phone(address.getPhone())
                .addressNote(address.getNote())
                .deliveryAmount(deliveryFee)
                .paymentMethod(request.getPaymentMethod())
                .shippingMethod(request.getShippingMethod())
                .status(status)
                .build();

        Set<OrderItem> items = new HashSet<>();
        for (CartItem i : cart.getItems()) {
            if (request.getCartItemIds().contains(i.getId())) {
                Product product = productRepository.findById(new ObjectId(i.getProductId())).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                Variant variant = product.getVariants().stream().filter(v -> v.getSku().equals(i.getSku())).findFirst().orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
                Inventory inventory = inventoryService.getInventoryByVariant(product.getId(), variant.getId());
                // Check available quantity of variant >= quantity of order item
                if (inventory.getAvailableStock() < i.getQuantity())
                    throw new ResourceNotFoundException("Not enough stock");
                // Reserved quantity
                inventory.setReservedQuantity(inventory.getReservedQuantity() + i.getQuantity());
                inventories.add(inventory);

                totalPrice = totalPrice.add(inventory.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())));

                OrderItem item = OrderItem.builder()
                        .productId(i.getProductId())
                        .sku(i.getSku())
                        .unitPrice(inventory.getPrice())
                        .quantity(i.getQuantity())
                        .totalPrice(inventory.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .order(order)
                        .build();
                items.add(item);
            }
        }
        totalPrice = totalPrice.add(deliveryFee);
        // Check price
        if (totalPrice.compareTo(request.getTotalPrice()) != 0)
            throw new PriceChangedException("Price was changed. Please try again");

        // Reserved quantity
        inventories.forEach(inventoryService::updateInventory);

        order.setItems(items);
        order = orderRepository.save(order);
        CreatePaymentResponse paymentResponse = processPayment(order);

        // remove cart items
        cartService.removeItems(auth, cart.getItems());
        return OrderCreationResponse.builder()
                .order(OrderResponse.mapperToOrderResponse(order))
                .payment(paymentResponse)
                .build();
    }

    private CreatePaymentResponse processPayment(Order order) {
        if (order.getStatus() != OrderStatus.AWAITING_PAYMENT) return null;
        CreatePaymentResponse paymentResponse = paymentService.createPaymentQR(order, "");
        return paymentResponse;
    }

    @Override
    public void handlePaymentCallback(String orderId, String paymentTransactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
