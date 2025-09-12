package com.pblues.sportsshop.service.inventory;

import com.pblues.sportsshop.common.constant.ErrorCode;
import com.pblues.sportsshop.common.exception.AppException;
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
        Inventory inventory =  inventoryRepository.findByProductIdAndVariantId(productId.toHexString(), variantId).orElseThrow(() -> new
                AppException(ErrorCode.INVENTORY_NOT_FOUND));
        return inventory;
    }

    @Override
    public void updateInventory(Inventory inventory) {
        inventoryRepository.save(inventory);
    }
}
