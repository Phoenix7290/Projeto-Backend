package org.pdv.repository.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pdv.domain.brand.Brand;
import org.pdv.repository.brand.models.BrandModel;
import org.pdv.service.brand.BrandRepository;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InFileBrandRepository implements BrandRepository {
    private final String filePath;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InFileBrandRepository(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.filePath = dirPath + File.separator + "brands.json";
    }

    @Override
    public String save(Brand brand) {
        List<BrandModel> brands = readFromFile();
        brands.add(BrandModel.fromDomain(brand));
        writeToFile(brands);
        return brand.getId();
    }

    @Override
    public void delete(String id) {
        List<BrandModel> brands = readFromFile();
        brands.removeIf(brand -> brand.id.equals(id));
        writeToFile(brands);
    }

    @Override
    public List<Brand> findAll() {
        return readFromFile().stream()
                .map(BrandModel::toDomain)
                .toList();
    }

    @Override
    public Brand findById(String id) {
        return readFromFile().stream()
                .filter(brand -> brand.id.equals(id))
                .findFirst()
                .map(BrandModel::toDomain)
                .orElse(null);
    }

    @Override
    public boolean existsByName(String name) {
        return readFromFile().stream()
                .anyMatch(brand -> brand.name.equals(name));
    }

    @Override
    public void update(Brand brand) {
        List<BrandModel> brands = readFromFile();
        Optional<BrandModel> existingBrand = brands.stream()
                .filter(b -> b.id.equals(brand.getId()))
                .findFirst();

        if (existingBrand.isPresent()) {
            brands.remove(existingBrand.get());
            brands.add(BrandModel.fromDomain(brand));
            writeToFile(brands);
        }
    }

    private List<BrandModel> readFromFile() {
        try {
            final var file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            final var fileValue = objectMapper.readValue(file, BrandModel[].class);
            return fileValue == null ? new ArrayList<>() : new ArrayList<>(List.of(fileValue));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read brands from file", e);
        }
    }

    private void writeToFile(List<BrandModel> brands) {
        try {
            final var file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (var writer = new FileWriter(file)) {
                objectMapper.writeValue(writer, brands);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write brands to file", e);
        }
    }
}
