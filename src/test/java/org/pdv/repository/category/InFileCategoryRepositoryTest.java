package org.pdv.repository.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdv.domain.category.Category;
import org.pdv.repository.category.models.CategoryModel;
import org.pdv.shared.JsonMapper;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InFileCategoryRepositoryTest {
    private final InFileCategoryRepository repository = new InFileCategoryRepository("testCategories");
    private final String filePath = "testCategories" + File.separator + "categories.json";

    @Test
    public void givenValidParams_whenSaveCategory_thenShouldSaveCategory() {
        // Arrange
        final var category = Category.with("1", "Category Name", "Category Description");

        // Act
        final var output = repository.save(category);

        // Assert
        assertEquals(category.getId(), output);

        final var fileValue = readFile();

        assertEquals(1, fileValue.size());

        final var firstValue = fileValue.get(0);
        assertEquals(category.getId(), firstValue.id);
        assertEquals(category.getName(), firstValue.name);
        assertEquals(category.getDescription(), firstValue.description);
    }

    @Test
    public void givenValidParams_whenDeleteCategory_thenShouldDeleteCategory() {
        // Arrange
        final var category = new CategoryModel("1", "Category Name", "Category Description");
        populateFile(List.of(category));

        // Act
        repository.delete(category.id);

        // Assert
        final var fileValue = readFile();
        assertEquals(0, fileValue.size());
    }

    @Test
    public void givenValidParams_whenFindAllCategories_thenShouldReturnAllCategories() {
        // Arrange
        final var category1 = new CategoryModel("1", "Category Name 1", "Category Description 1");
        final var category2 = new CategoryModel("2", "Category Name 2", "Category Description 2");
        populateFile(List.of(category1, category2));

        // Act
        final var output = repository.findAll();

        // Assert
        assertEquals(2, output.size());

        final var output1 = output.get(0);
        assertEquals(category1.id, output1.getId());
        assertEquals(category1.name, output1.getName());
        assertEquals(category1.description, output1.getDescription());

        final var output2 = output.get(1);
        assertEquals(category2.id, output2.getId());
        assertEquals(category2.name, output2.getName());
        assertEquals(category2.description, output2.getDescription());
    }

    @Test
    public void givenValidParams_whenFindCategoryById_thenShouldReturnCategory() {
        // Arrange
        final var category = new CategoryModel("1", "Category Name", "Category Description");
        populateFile(List.of(category));

        // Act
        final var output = repository.findById(category.id);

        // Assert
        assertEquals(category.id, output.getId());
        assertEquals(category.name, output.getName());
        assertEquals(category.description, output.getDescription());
    }

    @Test
    public void givenValidParams_whenUpdateCategory_thenShouldUpdateCategory() {
        // Arrange
        final var oldCategory = new CategoryModel("1", "Category Name", "Category Description");
        populateFile(List.of(oldCategory));

        final var input = Category.with("1", "Updated Category Name", "Updated Category Description");

        // Act
        repository.update(input);

        // Assert
        final var fileValue = readFile();
        assertEquals(1, fileValue.size());

        final var firstValue = fileValue.get(0);
        assertEquals(input.getId(), firstValue.id);
        assertEquals(input.getName(), firstValue.name);
        assertEquals(input.getDescription(), firstValue.description);
    }

    @Test
    public void givenValidParams_whenExistsByName_thenShouldReturnTrue() {
        // Arrange
        final var category = new CategoryModel("1", "Category Name", "Category Description");
        populateFile(List.of(category));

        // Act
        final var exists = repository.existsByName(category.name);

        // Assert
        assertTrue(exists);
    }

    @Test
    public void givenValidParams_whenExistsByName_thenShouldReturnFalse() {
        // Arrange
        final var category = new CategoryModel("1", "Category Name", "Category Description");
        populateFile(List.of(category));

        // Act
        final var exists = repository.existsByName("Non-existing name");

        // Assert
        assertFalse(exists);
    }

    private List<CategoryModel> readFile() {
        final var file = new File(filePath);

        try {
            final var value = JsonMapper.get().readValue(file, CategoryModel[].class);
            return value == null ? List.of() : List.of(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void populateFile(List<CategoryModel> value) {
        final var file = new File(filePath);

        try {
            JsonMapper.get().writeValue(file, value);
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
