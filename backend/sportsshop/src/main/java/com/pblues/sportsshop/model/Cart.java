package com.pblues.sportsshop.model;

import com.pblues.sportsshop.model.subdocument.CartItem;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "carts")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Cart {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private Long userId;
    private Set<CartItem> items = new HashSet<>();
}
