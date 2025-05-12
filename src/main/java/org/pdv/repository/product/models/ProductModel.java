package org.pdv.repository.product.models;

import org.pdv.domain.product.Product;

public class ProductModel {
    public String id;
    public String name;
    public String description;
    public double price;
    public int stock;
    public String categoryId;
    public String brandId;

    public ProductModel(
            String id,
            String name,
            String description,
            double price,
            int stock,
            String categoryId,
            String brandId
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.brandId = brandId;
    }

    public ProductModel() {
    }

    public static ProductModel fromDomain(Product product) {
        return new ProductModel(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategoryId(),
                product.getBrandId()
        );
    }

    public Product toDomain() {
        return Product.with(
                id,
                name,
                description,
                price,
                stock,
                categoryId,
                brandId
        );
    }
}
