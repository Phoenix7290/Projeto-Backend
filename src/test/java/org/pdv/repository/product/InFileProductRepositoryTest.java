package org.pdv.repository.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdv.domain.product.Product;
import org.pdv.repository.product.models.ProductModel;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InFileProductRepositoryTest {
    private final InFileProductRepository repository = new InFileProductRepository("testProducts");
    private final String filePath = "testProducts" + java.io.File.separator + "products.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void givenValidParams_whenSaveCategory_thenShouldSaveCategory() {
        // Arrange
        final var product = Product.with(
                "1",
                "Product Name",
                "Product Description",
                10.0,
                100,
                "categoryId",
                "brandId"
        );

        // Act
        final var output = repository.save(product);

        // Assert
        assertEquals(product.getId(), output);

        final var fileValue = readFile();

        assertEquals(1, fileValue.size());

        final var firstValue = fileValue.get(0);
        assertEquals(product.getId(), firstValue.id);
        assertEquals(product.getName(), firstValue.name);
        assertEquals(product.getDescription(), firstValue.description);
        assertEquals(product.getPrice(), firstValue.price);
        assertEquals(product.getStock(), firstValue.stock);
        assertEquals(product.getCategoryId(), firstValue.categoryId);
        assertEquals(product.getBrandId(), firstValue.brandId);
    }

    @Test
    public void givenValidParams_whenDeleteProduct_thenShouldDeleteProduct() {
        // Arrange
        final var product = new ProductModel(
                "1",
                "Product Name",
                "Product Description",
                10.0,
                100,
                "categoryId",
                "brandId"
        );
        populateFile(List.of(product));

        // Act
        repository.delete(product.id);

        // Assert
        final var fileValue = readFile();
        assertEquals(0, fileValue.size());
    }

    @Test
    public void givenValidParams_whenFindAllProducts_thenShouldReturnAllProducts() {
        // Arrange
        final var product1 = new ProductModel(
                "1",
                "Product Name",
                "Product Description",
                10.0,
                100,
                "categoryId",
                "brandId"
        );
        final var product2 = new ProductModel(
                "2",
                "Product Name 2",
                "Product Description 2",
                20.0,
                200,
                "categoryId",
                "brandId"
        );
        populateFile(List.of(product1, product2));

        // Act
        final var output = repository.findAll();

        // Assert
        assertEquals(2, output.size());

        final var output1 = output.get(0);
        assertEquals(product1.id, output1.getId());
        assertEquals(product1.name, output1.getName());
        assertEquals(product1.description, output1.getDescription());
        assertEquals(product1.price, output1.getPrice());
        assertEquals(product1.stock, output1.getStock());
        assertEquals(product1.categoryId, output1.getCategoryId());
        assertEquals(product1.brandId, output1.getBrandId());

        final var output2 = output.get(1);
        assertEquals(product2.id, output2.getId());
        assertEquals(product2.name, output2.getName());
        assertEquals(product2.description, output2.getDescription());
        assertEquals(product2.price, output2.getPrice());
        assertEquals(product2.stock, output2.getStock());
        assertEquals(product2.categoryId, output2.getCategoryId());
        assertEquals(product2.brandId, output2.getBrandId());
    }

    @Test
    public void givenValidParams_whenFindProductById_thenShouldReturnProduct() {
        // Arrange
        final var product = new ProductModel(
                "1",
                "Product Name",
                "Product Description",
                10.0,
                100,
                "categoryId",
                "brandId"
        );
        populateFile(List.of(product));

        // Act
        final var output = repository.findById(product.id);

        // Assert
        assertEquals(product.id, output.getId());
        assertEquals(product.name, output.getName());
        assertEquals(product.description, output.getDescription());
        assertEquals(product.price, output.getPrice());
        assertEquals(product.stock, output.getStock());
        assertEquals(product.categoryId, output.getCategoryId());
        assertEquals(product.brandId, output.getBrandId());
    }

    @Test
    public void givenValidParams_whenUpdateProduct_thenShouldUpdateProduct() {
        // Arrange
        final var oldProduct = new ProductModel(
                "1",
                "Old Product Name",
                "Old Product Description",
                5.0,
                50,
                "old_categoryId",
                "old_brandId"
        );
        populateFile(List.of(oldProduct));

        final var input = Product.with(
                "1",
                "Product Name",
                "Product Description",
                10.0,
                100,
                "categoryId",
                "brandId"
        );

        // Act
        repository.update(input);

        // Assert
        final var fileValue = readFile();
        assertEquals(1, fileValue.size());

        final var firstValue = fileValue.get(0);
        assertEquals(input.getId(), firstValue.id);
        assertEquals(input.getName(), firstValue.name);
        assertEquals(input.getDescription(), firstValue.description);
        assertEquals(input.getPrice(), firstValue.price);
        assertEquals(input.getStock(), firstValue.stock);
        assertEquals(input.getCategoryId(), firstValue.categoryId);
        assertEquals(input.getBrandId(), firstValue.brandId);
    }

    @Test
    public void givenValidParams_whenExistsByName_thenShouldReturnTrue() {
        // Arrange
        final var product = new ProductModel(
                "1",
                "Product Name",
                "Product Description",
                10.0,
                100,
                "categoryId",
                "brandId"
        );
        populateFile(List.of(product));

        // Act
        final var exists = repository.existsByName(product.name);

        // Assert
        assertTrue(exists);
    }

    @Test
    public void givenValidParams_whenExistsByName_thenShouldReturnFalse() {
        // Arrange
        final var product = new ProductModel(
                "1",
                "Product Name",
                "Product Description",
                10.0,
                100,
                "categoryId",
                "brandId"
        );
        populateFile(List.of(product));

        // Act
        final var exists = repository.existsByName("Non-existing name");

        // Assert
        assertFalse(exists);
    }

    private List<ProductModel> readFile() {
        final var file = new File(filePath);

        try {
            final var fileValue = objectMapper.readValue(file, ProductModel[].class);
            return fileValue == null ? List.of() : List.of(fileValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void populateFile(List<ProductModel> value) {
        final var file = new File(filePath);

        try {
            objectMapper.writeValue(file, value);
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
