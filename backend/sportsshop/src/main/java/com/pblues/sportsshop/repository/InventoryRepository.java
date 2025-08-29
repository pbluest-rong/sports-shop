package com.pblues.sportsshop.repository;

import com.pblues.sportsshop.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndVariantId(String hexString, String variantId);
}
