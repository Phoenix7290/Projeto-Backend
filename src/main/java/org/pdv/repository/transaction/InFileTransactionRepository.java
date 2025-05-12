package org.pdv.repository.transaction;

import org.pdv.domain.transaction.Transaction;
import org.pdv.repository.transaction.models.TransactionModel;
import org.pdv.service.transaction.TransactionRepository;
import org.pdv.shared.JsonMapper;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class InFileTransactionRepository implements TransactionRepository {
    private final String filePath;

    public InFileTransactionRepository(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        this.filePath = dirPath + File.separator + "transactions.json";
    }

    @Override
    public String save(Transaction transaction) {
        List<TransactionModel> values = readFromFile();
        values.add(TransactionModel.fromDomain(transaction));
        writeToFile(values);
        return transaction.getId();
    }

    @Override
    public void delete(String id) {
        List<TransactionModel> values = readFromFile();
        values.removeIf(value -> value.id.equals(id));
        writeToFile(values);
    }

    @Override
    public List<Transaction> findAll() {
        return readFromFile().stream()
                .map(TransactionModel::toDomain)
                .toList();
    }

    @Override
    public Transaction findById(String id) {
        return readFromFile().stream()
                .filter(value -> value.id.equals(id))
                .findFirst()
                .map(TransactionModel::toDomain)
                .orElse(null);
    }

    private List<TransactionModel> readFromFile() {
        try {
            final var file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            final var fileValue = JsonMapper.get().readValue(file, TransactionModel[].class);
            return fileValue == null ? new ArrayList<>() : new ArrayList<>(List.of(fileValue));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read transactions from file", e);
        }
    }

    private void writeToFile(List<TransactionModel> value) {
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
            throw new RuntimeException("Failed to write transactions to file", e);
        }
    }
}
