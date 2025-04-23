package org.pdv.service.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdv.domain.category.Category;
import org.pdv.domain.error.DomainException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void givenValidParams_whenCreateCategory_thenReturnCategory() throws DomainException {
        // Arrange
        final var input = new CategoryInput("Electronics", "Devices and gadgets");

        when(categoryRepository.existsByName(any()))
                .thenReturn(false);

        when(categoryRepository.save(any()))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    return category.getId();
                });

        // Act
        final var output = categoryService.createCategory(input);

        // Assert
        assertNotNull(output.id());
        assertEquals(input.name(), output.name());
        assertEquals(input.description(), output.description());

        verify(categoryRepository, times(1)).existsByName(eq(input.name()));
        verify(categoryRepository, times(1)).save(argThat(category ->
                Objects.nonNull(category.getId()) &&
                        Objects.equals(category.getName(), input.name()) &&
                        Objects.equals(category.getDescription(), input.description())
        ));
    }

    @Test
    public void givenInvalidParams_whenCreateCategory_thenThrowException() {
        // Arrange
        final var expectedErrorCount = 2;
        final var expectedErrorMessages = List.of(
                "'name' should not be empty",
                "'description' should not be empty"
        );

        final var input = new CategoryInput("", "");

        // Act
        final var exception = assertThrows(DomainException.class, () ->
                categoryService.createCategory(input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessages.get(0), exception.getErrors().get(0).getMessage());
        assertEquals(expectedErrorMessages.get(1), exception.getErrors().get(1).getMessage());
    }

    @Test
    public void givenExistentCategoryName_whenCreateCategory_thenThrowException() {
        // Arrange
        final var expectedErrorMessage = "'name' already exists";
        final var input = new CategoryInput("Electronics", "Devices and gadgets");

        when(categoryRepository.existsByName(any()))
                .thenReturn(true);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                categoryService.createCategory(input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(categoryRepository, times(1)).existsByName(eq(input.name()));
    }

    @Test
    public void givenValidParams_whenDeleteCategory_thenReturnNothing() {
        // Arrange
        final var id = "1234";
        doNothing().when(categoryRepository).delete(any());

        // Act
        categoryService.deleteCategory(id);

        // Assert
        verify(categoryRepository, times(1)).delete(eq(id));
    }

    @Test
    public void givenValidParams_whenListCategories_thenReturnListOfCategories() {
        // Arrange
        final var category1 = Category.with("id-1", "Electronics", "Devices and gadgets");
        final var category2 = Category.with("id-2", "Books", "Literature and novels");

        when(categoryRepository.findAll())
                .thenReturn(List.of(category1, category2));

        // Act
        final var categories = categoryService.listAllCategories();

        // Assert
        assertNotNull(categories);
        assertEquals(2, categories.size());

        assertEquals(category1.getId(), categories.get(0).id());
        assertEquals(category1.getName(), categories.get(0).name());
        assertEquals(category1.getDescription(), categories.get(0).description());

        assertEquals(category2.getId(), categories.get(1).id());
        assertEquals(category2.getName(), categories.get(1).name());
        assertEquals(category2.getDescription(), categories.get(1).description());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void givenValidParams_whenGetCategory_thenReturnCategory() {
        // Arrange
        final var id = "1234";
        final var category = Category.with(id, "Electronics", "Devices and gadgets");

        when(categoryRepository.findById(eq(id)))
                .thenReturn(category);

        // Act
        final var output = categoryService.getCategory(id);

        // Assert
        assertNotNull(output);
        assertEquals(category.getId(), output.id());
        assertEquals(category.getName(), output.name());
        assertEquals(category.getDescription(), output.description());

        verify(categoryRepository, times(1)).findById(eq(id));
    }

    @Test
    public void givenValidParams_whenUpdateCategory_thenReturnUpdatedCategory() throws DomainException {
        // Arrange
        final var id = "1234";
        final var input = new CategoryInput("Electronics", "Updated description");
        final var updatedCategory = Category.with(id, input.name(), input.description());

        when(categoryRepository.findById(eq(id)))
                .thenReturn(Category.with(id, "Old Name", "Old Description"));

        doNothing().when(categoryRepository).update(any());

        // Act
        final var output = categoryService.updateCategory(id, input);

        // Assert
        assertNotNull(output);
        assertEquals(updatedCategory.getId(), output.id());
        assertEquals(updatedCategory.getName(), output.name());
        assertEquals(updatedCategory.getDescription(), output.description());

        verify(categoryRepository, times(1)).findById(eq(id));
        verify(categoryRepository, times(1)).update(argThat(category ->
                Objects.equals(category.getId(), id) &&
                        Objects.equals(category.getName(), input.name()) &&
                        Objects.equals(category.getDescription(), input.description())
        ));
    }

    @Test
    public void givenInvalidParams_whenUpdateCategory_thenThrowException() {
        // Arrange
        final var id = "1234";
        final var expectedErrorCount = 2;
        final var expectedErrorMessages = List.of(
                "'name' should not be empty",
                "'description' should not be empty"
        );
        final var input = new CategoryInput("", "");

        when(categoryRepository.findById(eq(id)))
                .thenReturn(Category.with(id, "Old Name", "Old Description"));

        // Act
        final var exception = assertThrows(DomainException.class, () ->
                categoryService.updateCategory(id, input));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessages.get(0), exception.getErrors().get(0).getMessage());
        assertEquals(expectedErrorMessages.get(1), exception.getErrors().get(1).getMessage());

        verify(categoryRepository, times(1)).findById(eq(id));
    }
}
