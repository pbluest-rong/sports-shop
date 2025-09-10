package com.pblues.sportsshop.service.inventory;

import com.pblues.sportsshop.model.Inventory;
import org.bson.types.ObjectId;

public interface InventoryService {
    Inventory getInventoryByVariant(ObjectId productId, String variantId);

    void updateInventory(Inventory inventory);
}
