package org.pdv.service.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdv.domain.error.DomainException;
import org.pdv.domain.product.Product;
import org.pdv.domain.transaction.OrderItem;
import org.pdv.domain.transaction.Payment;
import org.pdv.domain.transaction.Transaction;
import org.pdv.domain.user.User;
import org.pdv.service.product.ProductRepository;
import org.pdv.service.user.UserRepository;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void givenValidParams_whenCreateTransaction_thenShouldReturnTransaction() throws DomainException {
        // Arrange
        final var seller = User.with(
                "Seller Name",
                "11111111111",
                "a@email.com"
        );
        final var buyer = User.with(
                "Buyer Name",
                "22222222222",
                "b@email.com"
        );
        final var product = Product.with(
                "Product Name",
                "Product Description",
                10.0,
                5,
                "categoryId",
                "brandId"
        );

        final var orderItemInput = new OrderItemInput(
                product.getId(),
                20
        );
        final var transactionInput = new TransactionInput(
                seller.getId(),
                buyer.getId(),
                Payment.CASH,
                List.of(orderItemInput)
        );

        when(userRepository.findById(seller.getId())).thenReturn(seller);
        when(userRepository.findById(buyer.getId())).thenReturn(buyer);
        when(productRepository.findById(product.getId())).thenReturn(product);
        when(transactionRepository.save(any())).thenAnswer(invocation -> {
            final var transaction = invocation.getArgument(0, Transaction.class);
            return transaction.getId();
        });

        // Act
        final var output = transactionService.createTransaction(transactionInput);

        // Assert
        assertNotNull(output);
        assertEquals(transactionInput.sellerId(), output.seller().id());
        assertEquals(seller.getName(), output.seller().name());
        assertEquals(transactionInput.buyerId(), output.buyer().id());
        assertEquals(buyer.getName(), output.buyer().name());
        assertEquals(transactionInput.payment(), output.payment());
        assertEquals(1, output.items().size());
        final var orderItemOutput = output.items().get(0);
        assertEquals(orderItemInput.productId(), orderItemOutput.product().id());
        assertEquals(product.getName(), orderItemOutput.product().name());
        assertEquals(product.getDescription(), orderItemOutput.product().description());
        assertEquals(product.getCategoryId(), orderItemOutput.product().categoryId());
        assertEquals(product.getBrandId(), orderItemOutput.product().brandId());
        assertEquals(orderItemInput.quantity(), orderItemOutput.quantity());
        assertEquals(20, output.totalQuantity());
        assertEquals(200, output.totalPrice());

        verify(userRepository, times(1)).findById(seller.getId());
        verify(userRepository, times(1)).findById(buyer.getId());
        verify(productRepository, times(1)).findById(product.getId());
        verify(transactionRepository, times(1)).save(argThat(
                transaction -> Objects.nonNull(transaction.getId()) &&
                        Objects.equals(transaction.getSellerId(), transactionInput.sellerId()) &&
                        Objects.equals(transaction.getBuyerId(), transactionInput.buyerId()) &&
                        Objects.equals(transaction.getPayment(), transactionInput.payment()) &&
                        transaction.getItems().size() == 1 &&
                        Objects.equals(transaction.getItems().get(0).getProductId(), orderItemInput.productId()) &&
                        Objects.equals(transaction.getItems().get(0).getQuantity(), orderItemInput.quantity())
        ));
    }

    @Test
    public void givenInvalidParams_whenCreateTransaction_thenThrowException() {
        // Arrange
        final var expectedErrorCount = 3;
        final var expectedErrorMessages = List.of(
                "'sellerId' should not be empty",
                "'buyerId' should not be empty",
                "'items' should not be empty"
        );

        final var transactionInput = new TransactionInput(
                "",
                "",
                Payment.CASH,
                List.of()
        );

        // Act
        final var exception = assertThrows(DomainException.class, () ->
                transactionService.createTransaction(transactionInput));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessages.get(0), exception.getErrors().get(0).getMessage());
        assertEquals(expectedErrorMessages.get(1), exception.getErrors().get(1).getMessage());
    }

    @Test
    public void givenNotExistentProductId_whenCreateTransaction_thenThrowException() {
        // Arrange
        final var expectedErrorMessage = "product 'product_id' not found";


        final var orderItemInput = new OrderItemInput(
                "product_id",
                20
        );
        final var transactionInput = new TransactionInput(
                "sellerId",
                "buyerId",
                Payment.CASH,
                List.of(orderItemInput)
        );

        when(productRepository.findById(any())).thenReturn(null);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(transactionInput));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(productRepository, times(1)).findById(orderItemInput.productId());
    }

    @Test
    public void givenNotExistentSellerId_whenCreateTransaction_thenThrowException() {
        // Arrange
        final var expectedErrorMessage = "'sellerId' not found";
        final var product = Product.with(
                "Product Name",
                "Product Description",
                10.0,
                5,
                "categoryId",
                "brandId"
        );

        final var orderItemInput = new OrderItemInput(
                product.getId(),
                20
        );
        final var transactionInput = new TransactionInput(
                "not-existent-seller-id",
                "buyerId",
                Payment.CASH,
                List.of(orderItemInput)
        );

        when(productRepository.findById(any())).thenReturn(product);
        when(userRepository.findById(transactionInput.sellerId())).thenReturn(null);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(transactionInput));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(productRepository, times(1)).findById(product.getId());
        verify(userRepository, times(1)).findById(transactionInput.sellerId());
    }

    @Test
    public void givenNotExistentBuyerId_whenCreateTransaction_thenThrowException() {
        // Arrange
        final var expectedErrorMessage = "'buyerId' not found";
        final var seller = User.with(
                "Seller Name",
                "11111111111",
                "a@email.com"
        );
        final var product = Product.with(
                "Product Name",
                "Product Description",
                10.0,
                5,
                "categoryId",
                "brandId"
        );

        final var orderItemInput = new OrderItemInput(
                product.getId(),
                20
        );
        final var transactionInput = new TransactionInput(
                seller.getId(),
                "invalid_buyer-id",
                Payment.CASH,
                List.of(orderItemInput)
        );

        when(productRepository.findById(any())).thenReturn(product);
        when(userRepository.findById(transactionInput.sellerId())).thenReturn(seller);
        when(userRepository.findById(transactionInput.buyerId())).thenReturn(null);

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(transactionInput));

        // Assert
        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(productRepository, times(1)).findById(product.getId());
        verify(userRepository, times(1)).findById(transactionInput.sellerId());
        verify(userRepository, times(1)).findById(transactionInput.buyerId());
    }

    @Test
    public void givenValidParams_whenDeleteTransaction_thenShouldDeleteTransaction() {
        // Arrange
        final var transactionId = "transaction_id";

        doNothing().when(transactionRepository).delete(transactionId);

        // Act
        transactionService.deleteTransaction(transactionId);

        // Assert
        verify(transactionRepository, times(1)).delete(transactionId);
    }

    @Test
    public void givenValidParams_whenListTransactions_thenShouldReturnListOfTransactions() {
        // Arrange
        final var seller = User.with(
                "Seller Name",
                "11111111111",
                "a@email.com"
        );
        final var buyer = User.with(
                "Buyer Name",
                "22222222222",
                "b@email.com"
        );
        final var product = Product.with(
                "Product Name",
                "Product Description",
                10.0,
                5,
                "categoryId",
                "brandId"
        );

        final var orderItem = OrderItem.with(
                product.getId(),
                2,
                product.getPrice()
        );
        final var transaction1 = Transaction.with(
                seller.getId(),
                buyer.getId(),
                Payment.CASH,
                List.of(orderItem)
        );
        final var transaction2 = Transaction.with(
                seller.getId(),
                buyer.getId(),
                Payment.CREDIT_CARD,
                List.of(orderItem)
        );

        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));
        when(userRepository.findById(seller.getId())).thenReturn(seller);
        when(userRepository.findById(buyer.getId())).thenReturn(buyer);
        when(productRepository.findById(product.getId())).thenReturn(product);

        // Act
        final var output = transactionService.listAllTransactions();

        // Assert
        assertNotNull(output);
        assertEquals(2, output.size());
        assertTransactionOutput(
                output.get(0),
                transaction1,
                seller,
                buyer,
                product
        );
        assertTransactionOutput(
                output.get(1),
                transaction2,
                seller,
                buyer,
                product
        );

        verify(transactionRepository, times(1)).findAll();
        verify(userRepository, times(2)).findById(seller.getId());
        verify(userRepository, times(2)).findById(buyer.getId());
        verify(productRepository, times(2)).findById(eq(product.getId()));
    }

    @Test
    public void givenValidParams_whenGetTransaction_thenReturnTransaction() {
        // Arrange
        final var seller = User.with(
                "Seller Name",
                "11111111111",
                "a@email.com"
        );
        final var buyer = User.with(
                "Buyer Name",
                "22222222222",
                "b@email.com"
        );
        final var product = Product.with(
                "Product Name",
                "Product Description",
                10.0,
                5,
                "categoryId",
                "brandId"
        );

        final var orderItem = OrderItem.with(
                product.getId(),
                2,
                product.getPrice()
        );
        final var transaction = Transaction.with(
                seller.getId(),
                buyer.getId(),
                Payment.CASH,
                List.of(orderItem)
        );

        when(transactionRepository.findById(transaction.getId())).thenReturn(transaction);
        when(userRepository.findById(seller.getId())).thenReturn(seller);
        when(userRepository.findById(buyer.getId())).thenReturn(buyer);
        when(productRepository.findById(product.getId())).thenReturn(product);

        // Act
        final var output = transactionService.getTransaction(transaction.getId());

        // Assert
        assertNotNull(output);
        assertTransactionOutput(
                output,
                transaction,
                seller,
                buyer,
                product
        );

        verify(transactionRepository, times(1)).findById(transaction.getId());
        verify(userRepository, times(1)).findById(seller.getId());
        verify(userRepository, times(1)).findById(buyer.getId());
        verify(productRepository, times(1)).findById(eq(product.getId()));
    }

    public void assertTransactionOutput(
            TransactionOutput output,
            Transaction transaction,
            User seller,
            User buyer,
            Product product
    ) {
        assertNotNull(output);
        assertEquals(transaction.getSellerId(), output.seller().id());
        assertEquals(seller.getName(), output.seller().name());
        assertEquals(transaction.getBuyerId(), output.buyer().id());
        assertEquals(buyer.getName(), output.buyer().name());
        assertEquals(transaction.getPayment(), output.payment());
        assertEquals(1, output.items().size());
        final var orderItemOutput = output.items().get(0);
        final var orderItem = transaction.getItems().get(0);
        assertEquals(orderItem.getProductId(), orderItemOutput.product().id());
        assertEquals(product.getName(), orderItemOutput.product().name());
        assertEquals(product.getDescription(), orderItemOutput.product().description());
        assertEquals(product.getCategoryId(), orderItemOutput.product().categoryId());
        assertEquals(product.getBrandId(), orderItemOutput.product().brandId());
        assertEquals(orderItem.getQuantity(), orderItemOutput.quantity());
        assertEquals(orderItem.getQuantity(), output.totalQuantity());
        assertEquals(orderItem.getQuantity() * product.getPrice(), output.totalPrice());
    }
}
