package org.pdv.service.transaction;

public record OrderItemOutput(
        String id,
        OrderItemProductOutput product,
        Integer quantity,
        Double price
) {
}
