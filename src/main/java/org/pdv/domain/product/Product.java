package org.pdv.domain.product;

import org.pdv.domain.error.DomainException;
import org.pdv.domain.utils.IdUtils;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private final String id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String categoryId;
    private String brandId;

    protected Product(
            String id,
            String name,
            String description,
            Double price,
            Integer stock,
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

    public static Product with(
            String id,
            String name,
            String description,
            Double price,
            Integer stock,
            String categoryId,
            String brandId
    ) {
        return new Product(
                id,
                name,
                description,
                price,
                stock,
                categoryId,
                brandId
        );
    }

    public static Product with(
            String name,
            String description,
            Double price,
            Integer stock,
            String categoryId,
            String brandId
    ) {
        return new Product(
                IdUtils.uuid(),
                name,
                description,
                price,
                stock,
                categoryId,
                brandId
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public void validate() throws DomainException {
        List<Error> errors = new ArrayList<>();

        if (name == null || name.isBlank()) {
            errors.add(new Error("'name' should not be empty"));
        }
        if (description == null || description.isBlank()) {
            errors.add(new Error("'description' should not be empty"));
        }
        if (price == null || price < 0) {
            errors.add(new Error("'price' should be a positive number"));
        }
        if (stock == null || stock < 0) {
            errors.add(new Error("'stock' should be a positive number"));
        }
        if (categoryId == null || categoryId.isBlank()) {
            errors.add(new Error("'categoryId' should not be empty"));
        }
        if (brandId == null || brandId.isBlank()) {
            errors.add(new Error("'brandId' should not be empty"));
        }

        if (!errors.isEmpty()) {
            throw DomainException.with(errors);
        }
    }
}
