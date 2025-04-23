package org.pdv.repository.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdv.domain.user.User;
import org.pdv.repository.user.models.UserModel;
import org.pdv.shared.JsonMapper;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InFileUserRepositoryTest {
    private final InFileUserRepository repository = new InFileUserRepository("testUsers");
    private final String filePath = "testUsers" + java.io.File.separator + "users.json";

    @Test
    public void givenValidParams_whenSaveUser_thenShouldSaveUser() {
        // Arrange
        final var user = User.with(
                "1",
                "User Name",
                "11111111111",
                "a@email.com"
        );

        // Act
        final var output = repository.save(user);

        // Assert
        assertEquals(user.getId(), output);

        final var fileValue = readFile();

        assertEquals(1, fileValue.size());

        final var firstValue = fileValue.get(0);
        assertEquals(user.getId(), firstValue.id);
        assertEquals(user.getName(), firstValue.name);
        assertEquals(user.getDocument(), firstValue.document);
        assertEquals(user.getEmail(), firstValue.email);
    }

    @Test
    public void givenValidParams_whenDeleteUser_thenShouldDeleteUser() {
        // Arrange
        final var userModel = new UserModel(
                "1",
                "User Name",
                "11111111111",
                "a@email.com"
        );
        populateFile(List.of(userModel));

        // Act
        repository.delete(userModel.id);

        // Assert
        final var fileValue = readFile();
        assertEquals(0, fileValue.size());
    }

    @Test
    public void givenValidParams_whenFindUserById_theShouldReturnUser() {
        // Arrange
        final var userModel = new UserModel(
                "1",
                "User Name",
                "11111111111",
                "a@email.com"
        );
        populateFile(List.of(userModel));

        // Act
        final var output = repository.findById("1");

        // Assert
        assertEquals(userModel.id, output.getId());
        assertEquals(userModel.name, output.getName());
        assertEquals(userModel.document, output.getDocument());
        assertEquals(userModel.email, output.getEmail());
    }

    private List<UserModel> readFile() {
        final var file = new File(filePath);

        try {
            final var fileValue = JsonMapper.get().readValue(file, UserModel[].class);
            return fileValue == null ? List.of() : List.of(fileValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void populateFile(List<UserModel> value) {
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
