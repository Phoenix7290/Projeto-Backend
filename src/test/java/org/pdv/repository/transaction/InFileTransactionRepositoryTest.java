package org.pdv.repository.transaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdv.domain.transaction.OrderItem;
import org.pdv.domain.transaction.Payment;
import org.pdv.domain.transaction.Status;
import org.pdv.domain.transaction.Transaction;
import org.pdv.domain.utils.InstantUtils;
import org.pdv.repository.transaction.models.OrderItemModel;
import org.pdv.repository.transaction.models.TransactionModel;
import org.pdv.shared.JsonMapper;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InFileTransactionRepositoryTest {
    private final InFileTransactionRepository repository = new InFileTransactionRepository("testTransactions");
    private final String filePath = "testTransactions" + java.io.File.separator + "transactions.json";

    @Test
    public void givenValidParams_whenSaveTransaction_thenShouldSaveTransaction() {
        // Arrange
        final var orderItem = OrderItem.with(
                "1",
                "productId",
                2,
                10.0
        );
        final var transaction = Transaction.with(
                "1",
                "buyerId",
                "sellerId",
                Payment.CASH,
                Status.COMPLETED,
                List.of(orderItem),
                InstantUtils.now()
        );

        // Act
        final var output = repository.save(transaction);

        // Assert
        assertEquals(transaction.getId(), output);

        final var fileValue = readFile();

        assertEquals(1, fileValue.size());

        final var firstValue = fileValue.get(0);
        assertEquals(transaction.getId(), firstValue.id);
        assertEquals(transaction.getBuyerId(), firstValue.buyerId);
        assertEquals(transaction.getSellerId(), firstValue.sellerId);
        assertEquals(transaction.getPayment(), firstValue.payment);
        assertEquals(transaction.getStatus(), firstValue.status);
        assertEquals(1, firstValue.items.size());

        final var orderItemValue = firstValue.items.get(0);
        assertEquals(orderItem.getId(), orderItemValue.id);
        assertEquals(orderItem.getProductId(), orderItemValue.productId);
        assertEquals(orderItem.getQuantity(), orderItemValue.quantity);
        assertEquals(orderItem.getPrice(), orderItemValue.price);

        assertEquals(transaction.getDate(), firstValue.date);
    }

    @Test
    public void givenValidParams_whenDeleteTransaction_thenShouldDeleteTransaction() {
        // Arrange
        final var orderItem = new OrderItemModel(
                "1",
                "productId",
                2,
                10.0
        );
        final var transaction = new TransactionModel(
                "1",
                "buyerId",
                "sellerId",
                Payment.CASH,
                Status.COMPLETED,
                List.of(orderItem),
                InstantUtils.now()
        );
        populateFile(List.of(transaction));

        // Act
        repository.delete(transaction.id);

        // Assert
        final var fileValue = readFile();
        assertEquals(0, fileValue.size());
    }

    @Test
    public void givenValidParams_whenFindAllTransactions_thenShouldReturnAllTransactions() {
        // Arrange
        final var orderItem = new OrderItemModel(
                "1",
                "productId",
                2,
                10.0
        );
        final var transaction1 = new TransactionModel(
                "1",
                "buyerId",
                "sellerId",
                Payment.CASH,
                Status.COMPLETED,
                List.of(orderItem),
                InstantUtils.now()
        );
        ;
        final var transaction2 = new TransactionModel(
                "2",
                "buyerId",
                "sellerId",
                Payment.CASH,
                Status.COMPLETED,
                List.of(orderItem),
                InstantUtils.now()
        );
        populateFile(List.of(transaction1, transaction2));

        // Act
        final var output = repository.findAll();

        // Assert
        assertEquals(2, output.size());

        final var output1 = output.get(0);
        assertEquals(transaction1.id, output1.getId());
        assertEquals(transaction1.sellerId, output1.getSellerId());
        assertEquals(transaction1.buyerId, output1.getBuyerId());
        assertEquals(transaction1.payment, output1.getPayment());
        assertEquals(transaction1.status, output1.getStatus());
        assertEquals(1, output1.getItems().size());
        assertEquals(transaction1.items.get(0).id, output1.getItems().get(0).getId());
        assertEquals(transaction1.items.get(0).productId, output1.getItems().get(0).getProductId());
        assertEquals(transaction1.items.get(0).quantity, output1.getItems().get(0).getQuantity());
        assertEquals(transaction1.items.get(0).price, output1.getItems().get(0).getPrice());
        assertEquals(transaction1.date, output1.getDate());

        final var output2 = output.get(1);
        assertEquals(transaction2.id, output2.getId());
        assertEquals(transaction2.sellerId, output2.getSellerId());
        assertEquals(transaction2.buyerId, output2.getBuyerId());
        assertEquals(transaction2.payment, output2.getPayment());
        assertEquals(transaction2.status, output2.getStatus());
        assertEquals(1, output2.getItems().size());
        assertEquals(transaction2.items.get(0).id, output2.getItems().get(0).getId());
        assertEquals(transaction2.items.get(0).productId, output2.getItems().get(0).getProductId());
        assertEquals(transaction2.items.get(0).quantity, output2.getItems().get(0).getQuantity());
        assertEquals(transaction2.items.get(0).price, output2.getItems().get(0).getPrice());
        assertEquals(transaction2.date, output2.getDate());
    }

    @Test
    public void givenValidParams_whenFindTransactionById_thenShouldReturnTransaction() {
        // Arrange
        final var orderItem = new OrderItemModel(
                "1",
                "productId",
                2,
                10.0
        );
        final var transaction = new TransactionModel(
                "1",
                "buyerId",
                "sellerId",
                Payment.CASH,
                Status.COMPLETED,
                List.of(orderItem),
                InstantUtils.now()
        );
        populateFile(List.of(transaction));

        // Act
        final var output = repository.findById(transaction.id);

        // Assert
        assertEquals(transaction.id, output.getId());
        assertEquals(transaction.sellerId, output.getSellerId());
        assertEquals(transaction.buyerId, output.getBuyerId());
        assertEquals(transaction.payment, output.getPayment());
        assertEquals(transaction.status, output.getStatus());
        assertEquals(1, output.getItems().size());
        assertEquals(transaction.items.get(0).id, output.getItems().get(0).getId());
        assertEquals(transaction.items.get(0).productId, output.getItems().get(0).getProductId());
        assertEquals(transaction.items.get(0).quantity, output.getItems().get(0).getQuantity());
        assertEquals(transaction.items.get(0).price, output.getItems().get(0).getPrice());
        assertEquals(transaction.date, output.getDate());
    }

    private List<TransactionModel> readFile() {
        final var file = new File(filePath);

        try {
            final var fileValue = JsonMapper.get().readValue(file, TransactionModel[].class);
            return fileValue == null ? List.of() : List.of(fileValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void populateFile(List<TransactionModel> value) {
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
