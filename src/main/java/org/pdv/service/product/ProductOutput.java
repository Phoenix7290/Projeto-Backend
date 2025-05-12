package org.pdv.service.product;

import org.pdv.service.brand.BrandOutput;
import org.pdv.service.category.CategoryOutput;

public record ProductOutput(
        String id,
        String name,
        String description,
        Double price,
        Integer stock,
        CategoryOutput category,
        BrandOutput brand
) {
}