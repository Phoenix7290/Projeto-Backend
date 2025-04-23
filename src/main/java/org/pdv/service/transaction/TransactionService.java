package org.pdv.service.transaction;

import org.pdv.domain.error.DomainException;
import org.pdv.domain.product.Product;
import org.pdv.domain.transaction.OrderItem;
import org.pdv.domain.transaction.Transaction;
import org.pdv.domain.user.User;
import org.pdv.service.product.ProductRepository;
import org.pdv.service.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public TransactionOutput createTransaction(TransactionInput aTransaction) throws DomainException {
        final var products = getProducts(aTransaction.items());
        final var transaction = transactionFromInput(aTransaction, products);
        transaction.validate();

        final var seller = userRepository.findById(aTransaction.sellerId());
        if (seller == null) {
            throw new IllegalArgumentException("'sellerId' not found");
        }

        final var buyer = userRepository.findById(aTransaction.buyerId());
        if (buyer == null) {
            throw new IllegalArgumentException("'buyerId' not found");
        }

        transactionRepository.save(transaction);

        return toTransactionOutput(
                transaction,
                seller,
                buyer,
                products
        );
    }

    public void deleteTransaction(String id) {
        transactionRepository.delete(id);
    }

    public List<TransactionOutput> listAllTransactions() {
        final var transactions = transactionRepository.findAll();
        return transactions.stream().map(transaction -> toTransactionOutput(
                transaction,
                userRepository.findById(transaction.getSellerId()),
                userRepository.findById(transaction.getBuyerId()),
                getProductsFromOrderItems(transaction.getItems())
        )).toList();
    }

    public TransactionOutput getTransaction(String id) {
        final var transaction = transactionRepository.findById(id);
        return toTransactionOutput(
                transaction,
                userRepository.findById(transaction.getSellerId()),
                userRepository.findById(transaction.getBuyerId()),
                getProductsFromOrderItems(transaction.getItems())
        );
    }

    private Transaction transactionFromInput(TransactionInput aTransaction, Map<String, Product> products) {
        return Transaction.with(
                aTransaction.sellerId(),
                aTransaction.buyerId(),
                aTransaction.payment(),
                getOrderItems(aTransaction.items(), products)
        );
    }

    private Map<String, Product> getProducts(List<OrderItemInput> orderItemInputs) {
        return orderItemInputs.stream()
                .map(orderItemInput -> {
                    final var product = productRepository.findById(orderItemInput.productId());
                    if (product == null) {
                        final var errorMsg = String.format("product '%s' not found", orderItemInput.productId());
                        throw new IllegalArgumentException(errorMsg);
                    }

                    return product;
                })
                .collect(Collectors.toMap(Product::getId, product -> product));
    }

    private Map<String, Product> getProductsFromOrderItems(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItemInput -> {
                    final var product = productRepository.findById(orderItemInput.getProductId());
                    if (product == null) {
                        final var errorMsg = String.format("product '%s' not found", orderItemInput.getProductId());
                        throw new IllegalArgumentException(errorMsg);
                    }

                    return product;
                })
                .collect(Collectors.toMap(Product::getId, product -> product));
    }

    private List<OrderItem> getOrderItems(List<OrderItemInput> orderItemInputs, Map<String, Product> products) {
        return orderItemInputs.stream()
                .map(orderItemInput ->
                        OrderItem.with(
                                orderItemInput.productId(),
                                orderItemInput.quantity(),
                                products.get(orderItemInput.productId()).getPrice()
                        ))
                .toList();
    }

    private TransactionOutput toTransactionOutput(Transaction transaction, User seller, User buyer, Map<String, Product> products) {
        final var sellerOutput = new TransactionUserOutput(
                seller.getId(),
                seller.getName()
        );

        final var buyerOutput = new TransactionUserOutput(
                buyer.getId(),
                buyer.getName()
        );

        final var OrderItemsOutput = transaction.getItems().stream()
                .map(item -> {
                    final var product = products.get(item.getProductId());
                    return new OrderItemOutput(
                            item.getId(),
                            new OrderItemProductOutput(
                                    item.getProductId(),
                                    product.getName(),
                                    product.getDescription(),
                                    product.getCategoryId(),
                                    product.getBrandId()
                            ),
                            item.getQuantity(),
                            item.getPrice()
                    );
                })
                .toList();

        return new TransactionOutput(
                transaction.getId(),
                sellerOutput,
                buyerOutput,
                transaction.getPayment(),
                OrderItemsOutput,
                transaction.getTotalQuantity(),
                transaction.getTotalPrice()
        );
    }
}
