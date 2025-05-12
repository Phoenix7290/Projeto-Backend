package org.pdv.service.product;

public record ProductInput(
        String name,
        String description,
        Double price,
        Integer stock,
        String categoryId,
        String brandId
) {
}
