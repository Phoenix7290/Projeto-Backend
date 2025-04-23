package org.pdv.service.transaction;

public record OrderItemInput(
        String productId,
        Integer quantity
) {
}
