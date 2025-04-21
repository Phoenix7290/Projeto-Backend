package org.pdv.domain.category;

import org.pdv.domain.error.DomainException;
import org.pdv.domain.utils.IdUtils;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private final String id;
    private String name;
    private String description;

    protected Category(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static Category with(String id, String name, String description) {
        return new Category(id, name, description);
    }

    public static Category with(String name, String description) {
        return new Category(IdUtils.uuid(), name, description);
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

    public void setDescription(String description){
        this.description = description;
    }

    public void validate() throws DomainException {
        List<Error> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add(new Error("'name' should not be empty"));
        }
        if (description == null || description.isBlank()) {
            errors.add(new Error("'description' should not be empty"));
        }

        if (!errors.isEmpty()) {
            throw DomainException.with(errors);
        }
    }
}
