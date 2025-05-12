package org.pdv.service.category;

import org.pdv.domain.category.Category;
import org.pdv.domain.error.DomainException;

import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryOutput createCategory(CategoryInput aCategory) throws DomainException {
        final var newCategory = Category.with(aCategory.name(), aCategory.description());
        newCategory.validate();

        final var exists = categoryRepository.existsByName(aCategory.name());
        if (exists) {
            throw new IllegalArgumentException("'name' already exists");
        }

        categoryRepository.save(newCategory);

        return new CategoryOutput(
                newCategory.getId(),
                newCategory.getName(),
                newCategory.getDescription()
        );
    }

    public void deleteCategory(String id) {
        categoryRepository.delete(id);
    }

    public List<CategoryOutput> listAllCategories() {
        final var categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategoryOutput(
                        category.getId(),
                        category.getName(),
                        category.getDescription()))
                .toList();
    }

    public CategoryOutput getCategory(String id) {
        final var category = categoryRepository.findById(id);

        return new CategoryOutput(
                category.getId(),
                category.getName(),
                category.getDescription());
    }

    public CategoryOutput updateCategory(String id, CategoryInput newCategory) throws DomainException {
        final var category = categoryRepository.findById(id);

        category.setName(newCategory.name());
        category.setDescription(newCategory.description());

        category.validate();

        categoryRepository.update(category);

        return new CategoryOutput(
                category.getId(),
                category.getName(),
                category.getDescription());
    }
}
