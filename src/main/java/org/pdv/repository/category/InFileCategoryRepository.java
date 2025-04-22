package org.pdv.repository.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pdv.domain.category.Category;
import org.pdv.repository.category.models.CategoryModel;
import org.pdv.service.category.CategoryRepository;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InFileCategoryRepository implements CategoryRepository {
    private final String filePath;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InFileCategoryRepository(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        this.filePath = dirPath + File.separator + "categories.json";
    }

    @Override
    public String save(Category category) {
        List<CategoryModel> values = readFromFile();
        values.add(CategoryModel.fromDomain(category));
        writeToFile(values);
        return category.getId();
    }

    @Override
    public void delete(String id) {
        List<CategoryModel> values = readFromFile();
        values.removeIf(category -> category.id.equals(id));
        writeToFile(values);
    }

    @Override
    public List<Category> findAll() {
        return readFromFile().stream()
                .map(CategoryModel::toDomain)
                .toList();
    }

    @Override
    public Category findById(String id) {
        return readFromFile().stream()
                .filter(value -> value.id.equals(id))
                .findFirst()
                .map(CategoryModel::toDomain)
                .orElse(null);
    }

    @Override
    public boolean existsByName(String name) {
        return readFromFile().stream()
                .anyMatch(value -> value.name.equals(name));
    }

    @Override
    public void update(Category category) {
        List<CategoryModel> values = readFromFile();
        Optional<CategoryModel> existingValue = values.stream()
                .filter(value -> value.id.equals(category.getId()))
                .findFirst();

        if (existingValue.isPresent()) {
            values.remove(existingValue.get());
            values.add(CategoryModel.fromDomain(category));
            writeToFile(values);
        }
    }

    private List<CategoryModel> readFromFile() {
        try {
            final var file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            final var fileValue = objectMapper.readValue(file, CategoryModel[].class);
            return fileValue == null ? new ArrayList<>() : new ArrayList<>(List.of(fileValue));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read categories from file", e);
        }
    }

    private void writeToFile(List<CategoryModel> value) {
        try {
            final var file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (var writer = new FileWriter(file)) {
                objectMapper.writeValue(writer, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write categories to file", e);
        }
    }
}
