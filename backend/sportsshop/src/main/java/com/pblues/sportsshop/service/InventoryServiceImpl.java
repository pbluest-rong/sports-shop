package com.pblues.sportsshop.service;

import com.pblues.sportsshop.exception.ResourceNotFoundException;
import com.pblues.sportsshop.model.Inventory;
import com.pblues.sportsshop.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory getInventoryByVariant(ObjectId productId, String variantId) {
        Inventory inventory =  inventoryRepository.findByProductIdAndVariantId(productId.toHexString(), variantId).orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        return inventory;
    }

    @Override
    public void updateInventory(Inventory inventory) {
        inventoryRepository.save(inventory);
    }
}
