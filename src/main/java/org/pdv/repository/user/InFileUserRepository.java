package org.pdv.repository.user;

import org.pdv.domain.user.User;
import org.pdv.repository.user.models.UserModel;
import org.pdv.service.user.UserRepository;
import org.pdv.shared.JsonMapper;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class InFileUserRepository implements UserRepository {
    private final String filePath;

    public InFileUserRepository(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        this.filePath = dirPath + File.separator + "users.json";
    }

    public String save(User user) {
        List<UserModel> values = readFromFile();
        values.add(UserModel.fromDomain(user));
        writeToFile(values);
        return user.getId();
    }

    @Override
    public void delete(String id) {
        List<UserModel> values = readFromFile();
        values.removeIf(value -> value.id.equals(id));
        writeToFile(values);
    }

    public User findById(String id) {
        return readFromFile().stream()
                .filter(value -> value.id.equals(id))
                .findFirst()
                .map(UserModel::toDomain)
                .orElse(null);
    }

    private List<UserModel> readFromFile() {
        try {
            final var file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            final var fileValue = JsonMapper.get().readValue(file, UserModel[].class);
            return fileValue == null ? new ArrayList<>() : new ArrayList<>(List.of(fileValue));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read users from file", e);
        }
    }

    private void writeToFile(List<UserModel> value) {
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
            throw new RuntimeException("Failed to write users to file", e);
        }
    }
}
