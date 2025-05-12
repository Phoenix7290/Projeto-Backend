package org.pdv.service.transaction;

public record OrderItemProductOutput(
        String id,
        String name,
        String description,
        String categoryId,
        String brandId
) {
}
