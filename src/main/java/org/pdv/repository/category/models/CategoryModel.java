package org.pdv.repository.category.models;

import org.pdv.domain.category.Category;

public class CategoryModel {
    public String id;
    public String name;
    public String description;

    public CategoryModel(
            String id,
            String name,
            String description
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public CategoryModel() {
    }

    public static CategoryModel fromDomain(Category category) {
        return new CategoryModel(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    public Category toDomain() {
        return Category.with(id, name, description);
    }
}
