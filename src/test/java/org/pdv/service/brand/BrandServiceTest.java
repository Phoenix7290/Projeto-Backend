package org.pdv.service.brand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdv.domain.brand.Brand;
import org.pdv.domain.error.DomainException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {
    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    public void givenValidParams_whenCreateBrand_thenReturnBrand() throws DomainException {
        // Arrange
        final var input = new BrandInput("Electronics", "Devices and gadgets");

        when(brandRepository.existsByName(any()))
                .thenReturn(false);

        when(brandRepository.save(any()))
                .thenAnswer(invocation -> {
                    Brand brand = invocation.getArgument(0);
                    return brand.getId();
                });

        // Act
        final var output = brandService.createBrand(input);

        // Assert
        assertNotNull(output.id());
        assertEquals(input.name(), output.name());
        assertEquals(input.description(), output.description());

        verify(brandRepository, times(1)).existsByName(eq(input.name()));
        verify(brandRepository, times(1)).save(argThat(brand ->
                Objects.nonNull(brand.getId()) &&
                        Objects.equals(brand.getName(), input.name()) &&
                        Objects.equals(brand.getDescription(), input.description())
        ));
    }

    @Test
    public void givenInvalidParams_whenCreateBrand_thenThrowException() {
        // Arrange
        final var expectedErrorCount = 2;
        final var expectedErrorMessages = List.of(
                "'name' should not be empty",
                "'description' should not be empty"
        );

        final var input = new BrandInput("", "");

        // Act
        final var exception = assertThrows(DomainException.class, () ->
                brandService.createBrand(input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessages.get(0), exception.getErrors().get(0).getMessage());
        assertEquals(expectedErrorMessages.get(1), exception.getErrors().get(1).getMessage());
    }

    @Test
    public void givenExistentBrandName_whenCreateBrand_thenThrowException() {
        // Arrange
        final var expectedErrorMessage = "'name' already exists";
        final var input = new BrandInput("Electronics", "Devices and gadgets");

        when(brandRepository.existsByName(any()))
                .thenReturn(true);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                brandService.createBrand(input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(brandRepository, times(1)).existsByName(eq(input.name()));
    }

    @Test
    public void givenValidParams_whenDeleteBrand_thenReturnNothing() {
        // Arrange
        final var id = "1234";

        doNothing().when(brandRepository).delete(any());

        // Act
        brandService.deleteBrand(id);

        // Assert
        verify(brandRepository, times(1)).delete(eq(id));
    }

    @Test
    public void givenValidParams_whenListBrands_thenReturnListOfBrands() {
        // Arrange
        final var brand1 = Brand.with("id-1", "Electronics", "Devices and gadgets");
        final var brand2 = Brand.with("id-2", "Books", "Literature and novels");

        when(brandRepository.findAll())
                .thenReturn(List.of(brand1, brand2));

        // Act
        final var brands = brandService.listAllBrands();

        // Assert
        assertNotNull(brands);
        assertEquals(2, brands.size());

        assertEquals(brand1.getId(), brands.get(0).id());
        assertEquals(brand1.getName(), brands.get(0).name());
        assertEquals(brand1.getDescription(), brands.get(0).description());

        assertEquals(brand2.getId(), brands.get(1).id());
        assertEquals(brand2.getName(), brands.get(1).name());
        assertEquals(brand2.getDescription(), brands.get(1).description());

        verify(brandRepository, times(1)).findAll();
    }

    @Test
    public void givenValidParams_whenGetBrand_thenReturnBrand() {
        // Arrange
        final var id = "1234";
        final var brand = Brand.with(id, "Electronics", "Devices and gadgets");

        when(brandRepository.findById(eq(id)))
                .thenReturn(brand);

        // Act
        final var output = brandService.getBrand(id);

        // Assert
        assertNotNull(output);
        assertEquals(brand.getId(), output.id());
        assertEquals(brand.getName(), output.name());
        assertEquals(brand.getDescription(), output.description());

        verify(brandRepository, times(1)).findById(eq(id));
    }

    @Test
    public void givenValidParams_whenUpdateBrand_thenReturnUpdatedBrand() throws DomainException {
        // Arrange
        final var id = "1234";
        final var input = new BrandInput("Electronics", "Updated description");
        final var updated = Brand.with(id, input.name(), input.description());

        when(brandRepository.findById(eq(id)))
                .thenReturn(Brand.with(id, "Old Name", "Old Description"));

        doNothing().when(brandRepository).update(any());

        // Act
        final var output = brandService.updateBrand(id, input);

        // Assert
        assertNotNull(output);
        assertEquals(updated.getId(), output.id());
        assertEquals(updated.getName(), output.name());
        assertEquals(updated.getDescription(), output.description());

        verify(brandRepository, times(1)).findById(eq(id));
        verify(brandRepository, times(1)).update(argThat(brand ->
                Objects.equals(brand.getId(), id) &&
                        Objects.equals(brand.getName(), input.name()) &&
                        Objects.equals(brand.getDescription(), input.description())
        ));
    }

    @Test
    public void givenInvalidParams_whenUpdateBrand_thenThrowException() {
        // Arrange
        final var id = "1234";
        final var expectedErrorCount = 2;
        final var expectedErrorMessages = List.of(
                "'name' should not be empty",
                "'description' should not be empty"
        );
        final var input = new BrandInput("", "");

        when(brandRepository.findById(eq(id)))
                .thenReturn(Brand.with(id, "Old Name", "Old Description"));

        // Act
        final var exception = assertThrows(DomainException.class, () ->
                brandService.updateBrand(id, input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessages.get(0), exception.getErrors().get(0).getMessage());
        assertEquals(expectedErrorMessages.get(1), exception.getErrors().get(1).getMessage());

        verify(brandRepository, times(1)).findById(eq(id));
    }
}
