package org.pdv.repository.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdv.domain.brand.Brand;
import org.pdv.repository.brand.models.BrandModel;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InFileBrandRepositoryTest {
    private final InFileBrandRepository repository = new InFileBrandRepository("testBrands");
    private final String filePath = "testBrands" + File.separator + "brands.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void givenValidParams_whenSaveBrand_thenShouldSaveBrand() {
        // Arrange
        final var brand = Brand.with("1", "Brand Name", "Brand Description");

        // Act
        final var output = repository.save(brand);

        // Assert
        assertEquals(brand.getId(), output);

        final var savedBrands = readFile();

        assertEquals(1, savedBrands.size());

        final var savedBrand = savedBrands.get(0);
        assertEquals(brand.getId(), savedBrand.id);
        assertEquals(brand.getName(), savedBrand.name);
        assertEquals(brand.getDescription(), savedBrand.description);
    }

    @Test
    public void givenValidParams_whenDeleteBrand_thenShouldDeleteBrand() {
        // Arrange
        final var brand = new BrandModel("1", "Brand Name", "Brand Description");
        populateFile(List.of(brand));

        // Act
        repository.delete(brand.id);

        // Assert
        final var savedBrands = readFile();
        assertEquals(0, savedBrands.size());
    }

    @Test
    public void givenValidParams_whenFindAllBrands_thenShouldReturnAllBrands() {
        // Arrange
        final var brand1 = new BrandModel("1", "Brand Name 1", "Brand Description 1");
        final var brand2 = new BrandModel("2", "Brand Name 2", "Brand Description 2");
        populateFile(List.of(brand1, brand2));

        // Act
        final var output = repository.findAll();

        // Assert
        assertEquals(2, output.size());

        final var output1 = output.get(0);
        assertEquals(brand1.id, output1.getId());
        assertEquals(brand1.name, output1.getName());
        assertEquals(brand1.description, output1.getDescription());

        final var output2 = output.get(1);
        assertEquals(brand2.id, output2.getId());
        assertEquals(brand2.name, output2.getName());
        assertEquals(brand2.description, output2.getDescription());
    }

    @Test
    public void givenValidParams_whenFindBrandById_thenShouldReturnBrand() {
        // Arrange
        final var brand = new BrandModel("1", "Brand Name", "Brand Description");
        populateFile(List.of(brand));

        // Act
        final var output = repository.findById(brand.id);

        // Assert
        assertEquals(brand.id, output.getId());
        assertEquals(brand.name, output.getName());
        assertEquals(brand.description, output.getDescription());
    }

    @Test
    public void givenValidParams_whenUpdateBrand_thenShouldUpdateBrand() {
        // Arrange
        final var oldBrand = new BrandModel("1", "Brand Name", "Brand Description");
        populateFile(List.of(oldBrand));

        final var brand = Brand.with("1", "Updated Brand Name", "Updated Brand Description");

        // Act
        repository.update(brand);

        // Assert
        final var savedBrands = readFile();
        assertEquals(1, savedBrands.size());

        final var savedBrand = savedBrands.get(0);
        assertEquals(brand.getId(), savedBrand.id);
        assertEquals(brand.getName(), savedBrand.name);
        assertEquals(brand.getDescription(), savedBrand.description);
    }

    @Test
    public void givenValidParams_whenExistsByName_thenShouldReturnTrue() {
        // Arrange
        final var brand = new BrandModel("1", "Brand Name", "Brand Description");
        populateFile(List.of(brand));

        // Act
        final var exists = repository.existsByName(brand.name);

        // Assert
        assertTrue(exists);
    }

    @Test
    public void givenValidParams_whenExistsByName_thenShouldReturnFalse() {
        // Arrange
        final var brand = new BrandModel("1", "Brand Name", "Brand Description");
        populateFile(List.of(brand));

        // Act
        final var exists = repository.existsByName("Non-existing Brand");

        // Assert
        assertFalse(exists);
    }

    private List<BrandModel> readFile() {
        final var file = new File(filePath);

        try {
            final var brandsFile = objectMapper.readValue(file, BrandModel[].class);
            return brandsFile == null ? List.of() : List.of(brandsFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void populateFile(List<BrandModel> brands) {
        final var file = new File(filePath);

        try {
            objectMapper.writeValue(file, brands);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
