package org.pdv.service.category;

import org.pdv.domain.category.Category;

import java.util.List;

public interface CategoryRepository {
    public String save(Category category);

    public void delete(String name);

    public List<Category> findAll();

    public Category findById(String id);

    public boolean existsByName(String name);

    public void update(Category category);
}
