package org.pdv.service.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdv.domain.brand.Brand;
import org.pdv.domain.category.Category;
import org.pdv.domain.error.DomainException;
import org.pdv.domain.product.Product;
import org.pdv.service.brand.BrandRepository;
import org.pdv.service.category.CategoryRepository;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void givenValidParams_whenCreateProduct_thenReturnProduct() throws DomainException {
        // Arrange
        final var input = new ProductInput(
                "Product Name",
                "Product Description",
                100.0,
                10,
                "categoryId",
                "brandId"
        );

        final var category = Category.with(
                "categoryId",
                "Category Name",
                "Category Description"
        );

        final var brand = Brand.with(
                "brandId",
                "Brand Name",
                "Brand Description"
        );

        when(productRepository.existsByName(any()))
                .thenReturn(false);

        when(categoryRepository.findById(any()))
                .thenReturn(category);

        when(brandRepository.findById(any()))
                .thenReturn(brand);

        when(productRepository.save(any()))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    return product.getId();
                });

        // Act
        final var output = productService.createProduct(input);

        // Assert
        assertNotNull(output.id());
        assertEquals(input.name(), output.name());
        assertEquals(input.description(), output.description());
        assertEquals(input.price(), output.price());
        assertEquals(input.stock(), output.stock());
        assertEquals(input.categoryId(), output.category().id());
        assertEquals(category.getName(), output.category().name());
        assertEquals(category.getDescription(), output.category().description());
        assertEquals(input.brandId(), output.brand().id());
        assertEquals(brand.getName(), output.brand().name());
        assertEquals(brand.getDescription(), output.brand().description());

        verify(productRepository, times(1)).existsByName(eq(input.name()));
        verify(categoryRepository, times(1)).findById(eq(input.categoryId()));
        verify(brandRepository, times(1)).findById(eq(input.brandId()));
        verify(productRepository, times(1)).save(argThat(product ->
                Objects.nonNull(product.getId()) &&
                        Objects.equals(product.getName(), input.name()) &&
                        Objects.equals(product.getDescription(), input.description()) &&
                        Objects.equals(product.getPrice(), input.price()) &&
                        Objects.equals(product.getStock(), input.stock()) &&
                        Objects.equals(product.getCategoryId(), input.categoryId()) &&
                        Objects.equals(product.getBrandId(), input.brandId())
        ));
    }

    @Test
    public void givenInvalidParams_whenCreateProduct_thenThrowException() {
        // Arrange
        final var expectedErrorCount = 6;
        final var expectedErrorMessages = List.of(
                "'name' should not be empty",
                "'description' should not be empty",
                "'price' should be a positive number",
                "'stock' should be a positive number",
                "'categoryId' should not be empty",
                "'brandId' should not be empty"
        );

        final var input = new ProductInput(
                "",
                "",
                -0.1,
                -1,
                null,
                null
        );

        // Act
        final var exception = assertThrows(DomainException.class, () ->
                productService.createProduct(input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessages.get(0), exception.getErrors().get(0).getMessage());
        assertEquals(expectedErrorMessages.get(1), exception.getErrors().get(1).getMessage());
        assertEquals(expectedErrorMessages.get(2), exception.getErrors().get(2).getMessage());
        assertEquals(expectedErrorMessages.get(3), exception.getErrors().get(3).getMessage());
        assertEquals(expectedErrorMessages.get(4), exception.getErrors().get(4).getMessage());
        assertEquals(expectedErrorMessages.get(5), exception.getErrors().get(5).getMessage());
    }

    @Test
    public void givenExistentProductName_whenCreateProduct_thenThrowException() {
        // Arrange
        final var expectedErrorMessage = "'name' already exists";
        final var input = new ProductInput(
                "Product Name",
                "Product Description",
                100.0,
                10,
                "categoryId",
                "brandId"
        );

        when(productRepository.existsByName(any()))
                .thenReturn(true);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                productService.createProduct(input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(productRepository, times(1)).existsByName(eq(input.name()));
    }

    @Test
    public void givenNotExistentCategoryId_whenCreateProduct_thenThrowException() {
        // Arrange
        final var expectedErrorMessage = "'categoryId' not found";
        final var input = new ProductInput(
                "Product Name",
                "Product Description",
                100.0,
                10,
                "wrongId",
                "brandId"
        );

        when(productRepository.existsByName(any()))
                .thenReturn(false);

        when(categoryRepository.findById(any()))
                .thenReturn(null);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                productService.createProduct(input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(productRepository, times(1)).existsByName(eq(input.name()));
        verify(categoryRepository, times(1)).findById(eq(input.categoryId()));
    }

    @Test
    public void givenNotExistentBrandId_whenCreateProduct_thenThrowException() {
        // Arrange
        final var expectedErrorMessage = "'brandId' not found";
        final var input = new ProductInput(
                "Product Name",
                "Product Description",
                100.0,
                10,
                "categoryId",
                "wrongId"
        );

        when(productRepository.existsByName(any()))
                .thenReturn(false);

        final var category = Category.with(
                "categoryId",
                "Category Name",
                "Category Description"
        );
        when(categoryRepository.findById(any()))
                .thenReturn(category);

        when(brandRepository.findById(any()))
                .thenReturn(null);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                productService.createProduct(input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(productRepository, times(1)).existsByName(eq(input.name()));
        verify(categoryRepository, times(1)).findById(eq(input.categoryId()));
        verify(brandRepository, times(1)).findById(eq(input.brandId()));
    }

    @Test
    public void givenValidParams_whenDeleteProduct_thenReturnNothing() {
        // Arrange
        final var input = "productId";
        doNothing().when(productRepository).delete(any());

        // Act
        productService.deleteProduct(input);

        // Assert
        verify(productRepository, times(1)).delete(input);
    }

    @Test
    public void givenValidParams_whenListProducts_thenReturnListOfProducts() {
        // Arrange
        final var product1 = Product.with(
                "productId1",
                "Product 1",
                "Description 1",
                100.0,
                10,
                "categoryId",
                "brandId"
        );

        final var product2 = Product.with(
                "productId2",
                "Product 2",
                "Description 2",
                200.0,
                20,
                "categoryId",
                "brandId"
        );

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        final var category = Category.with(
                "categoryId",
                "Category Name",
                "Category Description"
        );
        when(categoryRepository.findById(any()))
                .thenReturn(category);

        final var brand = Brand.with(
                "brandId",
                "Brand Name",
                "Brand Description"
        );
        when(brandRepository.findById(any()))
                .thenReturn(brand);

        // Act
        final var output = productService.listAllProducts();

        // Assert
        assertNotNull(output);
        assertEquals(2, output.size());

        final var output1 = output.get(0);
        assertEquals(product1.getId(), output1.id());
        assertEquals(product1.getName(), output1.name());
        assertEquals(product1.getDescription(), output1.description());
        assertEquals(product1.getPrice(), output1.price());
        assertEquals(product1.getStock(), output1.stock());
        assertEquals(product1.getCategoryId(), output1.category().id());
        assertEquals(category.getName(), output1.category().name());
        assertEquals(category.getDescription(), output1.category().description());
        assertEquals(product1.getBrandId(), output1.brand().id());
        assertEquals(brand.getName(), output1.brand().name());
        assertEquals(brand.getDescription(), output1.brand().description());

        final var output2 = output.get(1);
        assertEquals(product2.getId(), output2.id());
        assertEquals(product2.getName(), output2.name());
        assertEquals(product2.getDescription(), output2.description());
        assertEquals(product2.getPrice(), output2.price());
        assertEquals(product2.getStock(), output2.stock());
        assertEquals(product2.getCategoryId(), output2.category().id());
        assertEquals(category.getName(), output2.category().name());
        assertEquals(category.getDescription(), output2.category().description());
        assertEquals(product2.getBrandId(), output2.brand().id());
        assertEquals(brand.getName(), output2.brand().name());
        assertEquals(brand.getDescription(), output2.brand().description());

        verify(productRepository, times(1)).findAll();
        verify(categoryRepository, times(2)).findById(eq(product1.getCategoryId()));
        verify(brandRepository, times(2)).findById(eq(product2.getBrandId()));
    }

    @Test
    public void givenValidParams_whenGetProduct_thenReturnProduct() {
        // Arrange
        final var id = "productId";
        final var product = Product.with(
                id,
                "Product Name",
                "Product Description",
                100.0,
                10,
                "categoryId",
                "brandId"
        );

        when(productRepository.findById(eq(id)))
                .thenReturn(product);

        final var category = Category.with(
                "categoryId",
                "Category Name",
                "Category Description"
        );
        when(categoryRepository.findById(any()))
                .thenReturn(category);

        final var brand = Brand.with(
                "brandId",
                "Brand Name",
                "Brand Description"
        );
        when(brandRepository.findById(any()))
                .thenReturn(brand);

        // Act
        final var output = productService.getProduct(id);

        // Assert
        assertNotNull(output);
        assertEquals(product.getId(), output.id());
        assertEquals(product.getName(), output.name());
        assertEquals(product.getDescription(), output.description());
        assertEquals(product.getPrice(), output.price());
        assertEquals(product.getStock(), output.stock());
        assertEquals(product.getCategoryId(), output.category().id());
        assertEquals(category.getName(), output.category().name());
        assertEquals(category.getDescription(), output.category().description());
        assertEquals(product.getBrandId(), output.brand().id());
        assertEquals(brand.getName(), output.brand().name());
        assertEquals(brand.getDescription(), output.brand().description());

        verify(productRepository, times(1)).findById(eq(id));
        verify(categoryRepository, times(1)).findById(eq(product.getCategoryId()));
        verify(brandRepository, times(1)).findById(eq(product.getBrandId()));
    }

    @Test
    public void givenValidParams_whenUpdateProduct_thenReturnUpdatedProduct() throws DomainException {
        // Arrange
        final var id = "productId";
        final var category = Category.with(
                "categoryId",
                "Category Name",
                "Category Description"
        );
        final var brand = Brand.with(
                "brandId",
                "Brand Name",
                "Brand Description"
        );
        final var input = new ProductInput(
                "Updated Name",
                "Updated Description",
                150.0,
                15,
                category.getId(),
                brand.getId()
        );

        when(productRepository.findById(eq(id)))
                .thenReturn(Product.with(
                        id,
                        "Old Name",
                        "Old Description",
                        100.0,
                        10,
                        "categoryId",
                        "brandId"
                ));

        doNothing().when(productRepository).update(any());

        when(categoryRepository.findById(any()))
                .thenReturn(category);

        when(brandRepository.findById(any()))
                .thenReturn(brand);

        // Act
        final var output = productService.updateProduct(id, input);

        // Assert
        assertNotNull(output.id());
        assertEquals(input.name(), output.name());
        assertEquals(input.description(), output.description());
        assertEquals(input.price(), output.price());
        assertEquals(input.stock(), output.stock());
        assertEquals(input.categoryId(), output.category().id());
        assertEquals(category.getName(), output.category().name());
        assertEquals(category.getDescription(), output.category().description());
        assertEquals(input.brandId(), output.brand().id());
        assertEquals(brand.getName(), output.brand().name());
        assertEquals(brand.getDescription(), output.brand().description());

        verify(productRepository, times(1)).findById(eq(id));
        verify(productRepository, times(1)).update(argThat(product ->
                Objects.nonNull(product.getId()) &&
                        Objects.equals(product.getName(), input.name()) &&
                        Objects.equals(product.getDescription(), input.description()) &&
                        Objects.equals(product.getPrice(), input.price()) &&
                        Objects.equals(product.getStock(), input.stock()) &&
                        Objects.equals(product.getCategoryId(), input.categoryId()) &&
                        Objects.equals(product.getBrandId(), input.brandId())
        ));
        verify(categoryRepository, times(1)).findById(eq(input.categoryId()));
        verify(brandRepository, times(1)).findById(eq(input.brandId()));
    }

    @Test
    public void givenInvalidParams_whenUpdateProduct_thenThrowException() {
        // Arrange
        final var id = "productId";
        final var expectedErrorCount = 6;
        final var expectedErrorMessages = List.of(
                "'name' should not be empty",
                "'description' should not be empty",
                "'price' should be a positive number",
                "'stock' should be a positive number",
                "'categoryId' should not be empty",
                "'brandId' should not be empty"
        );

        final var input = new ProductInput(
                "",
                "",
                -0.1,
                -1,
                null,
                null
        );

        when(productRepository.findById(eq(id)))
                .thenReturn(Product.with(
                        id,
                        "Old Name",
                        "Old Description",
                        100.0,
                        10,
                        "categoryId",
                        "brandId"
                ));

        // Act
        final var exception = assertThrows(DomainException.class, () ->
                productService.updateProduct(id, input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessages.get(0), exception.getErrors().get(0).getMessage());
        assertEquals(expectedErrorMessages.get(1), exception.getErrors().get(1).getMessage());
        assertEquals(expectedErrorMessages.get(2), exception.getErrors().get(2).getMessage());
        assertEquals(expectedErrorMessages.get(3), exception.getErrors().get(3).getMessage());
        assertEquals(expectedErrorMessages.get(4), exception.getErrors().get(4).getMessage());
        assertEquals(expectedErrorMessages.get(5), exception.getErrors().get(5).getMessage());
    }

    @Test
    public void givenNotExistentProductId_whenUpdateProduct_thenThrowException() {
        // Arrange
        final var id = "productId";
        final var expectedErrorMessage = "'id' not found";
        final var input = new ProductInput(
                "Updated Name",
                "Updated Description",
                150.0,
                15,
                "categoryId",
                "brandId"
        );

        when(productRepository.findById(eq(id)))
                .thenReturn(null);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                productService.updateProduct(id, input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(productRepository, times(1)).findById(eq(id));
    }

    @Test
    public void givenNotExistentCategoryId_whenUpdateProduct_thenThrowException() {
        // Arrange
        final var id = "productId";
        final var expectedErrorMessage = "'categoryId' not found";
        final var input = new ProductInput(
                "Updated Name",
                "Updated Description",
                150.0,
                15,
                "wrongId",
                "brandId"
        );

        when(productRepository.findById(eq(id)))
                .thenReturn(Product.with(
                        id,
                        "Old Name",
                        "Old Description",
                        100.0,
                        10,
                        "categoryId",
                        "brandId"
                ));

        when(categoryRepository.findById(any()))
                .thenReturn(null);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                productService.updateProduct(id, input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(productRepository, times(1)).findById(eq(id));
        verify(categoryRepository, times(1)).findById(eq(input.categoryId()));
    }

    @Test
    public void givenNotExistentBrandId_whenUpdateProduct_thenThrowException() {
        // Arrange
        final var id = "productId";
        final var expectedErrorMessage = "'brandId' not found";
        final var category = Category.with(
                "categoryId",
                "Category Name",
                "Category Description"
        );
        final var input = new ProductInput(
                "Updated Name",
                "Updated Description",
                150.0,
                15,
                category.getId(),
                "wrongId"
        );

        when(productRepository.findById(eq(id)))
                .thenReturn(Product.with(
                        id,
                        "Old Name",
                        "Old Description",
                        100.0,
                        10,
                        "categoryId",
                        "brandId"
                ));

        when(categoryRepository.findById(any()))
                .thenReturn(category);

        when(brandRepository.findById(any()))
                .thenReturn(null);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                productService.updateProduct(id, input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(productRepository, times(1)).findById(eq(id));
        verify(categoryRepository, times(1)).findById(eq(input.categoryId()));
        verify(brandRepository, times(1)).findById(eq(input.brandId()));
    }
}
