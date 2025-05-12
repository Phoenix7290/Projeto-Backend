package org.pdv.repository.product;

import org.pdv.domain.product.Product;
import org.pdv.repository.product.models.ProductModel;
import org.pdv.service.product.ProductRepository;
import org.pdv.shared.JsonMapper;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InFileProductRepository implements ProductRepository {
    private final String filePath;

    public InFileProductRepository(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        this.filePath = dirPath + File.separator + "products.json";
    }

    @Override
    public String save(Product product) {
        List<ProductModel> values = readFromFile();
        values.add(ProductModel.fromDomain(product));
        writeToFile(values);
        return product.getId();
    }

    @Override
    public void delete(String id) {
        List<ProductModel> values = readFromFile();
        values.removeIf(value -> value.id.equals(id));
        writeToFile(values);
    }

    @Override
    public List<Product> findAll() {
        return readFromFile().stream()
                .map(ProductModel::toDomain)
                .toList();
    }

    @Override
    public Product findById(String id) {
        return readFromFile().stream()
                .filter(value -> value.id.equals(id))
                .findFirst()
                .map(ProductModel::toDomain)
                .orElse(null);
    }

    @Override
    public boolean existsByName(String name) {
        return readFromFile().stream()
                .anyMatch(value -> value.name.equals(name));
    }

    @Override
    public void update(Product product) {
        List<ProductModel> values = readFromFile();
        Optional<ProductModel> existingValue = values.stream()
                .filter(value -> value.id.equals(product.getId()))
                .findFirst();

        if (existingValue.isPresent()) {
            values.remove(existingValue.get());
            values.add(ProductModel.fromDomain(product));
            writeToFile(values);
        }
    }

    private List<ProductModel> readFromFile() {
        try {
            final var file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            final var fileValue = JsonMapper.get().readValue(file, ProductModel[].class);
            return fileValue == null ? new ArrayList<>() : new ArrayList<>(List.of(fileValue));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read products from file", e);
        }
    }

    private void writeToFile(List<ProductModel> value) {
        try {
            final var file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (var writer = new FileWriter(file)) {
                JsonMapper.get().writeValue(writer, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write products to file", e);
        }
    }
}
