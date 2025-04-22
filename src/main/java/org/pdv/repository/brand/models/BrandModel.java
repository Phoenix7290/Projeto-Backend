package org.pdv.repository.brand.models;

import org.pdv.domain.brand.Brand;

public class BrandModel {
    public String id;
    public String name;
    public String description;

    public BrandModel(
            String id,
            String name,
            String description
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public BrandModel() {
    }

    public static BrandModel fromDomain(Brand brand) {
        return new BrandModel(
                brand.getId(),
                brand.getName(),
                brand.getDescription()
        );
    }

    public Brand toDomain() {
        return Brand.with(id, name, description);
    }
}