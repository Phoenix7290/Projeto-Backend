package org.pdv.repository.transaction.models;

import org.pdv.domain.transaction.Payment;
import org.pdv.domain.transaction.Status;
import org.pdv.domain.transaction.Transaction;

import java.time.Instant;
import java.util.List;

public class TransactionModel {
    public String id;
    public String sellerId;
    public String buyerId;
    public Payment payment;
    public Status status;
    public List<OrderItemModel> items;
    public Instant date;

    public TransactionModel(
            String id,
            String sellerId,
            String buyerId,
            Payment payment,
            Status status,
            List<OrderItemModel> items,
            Instant date
    ) {
        this.id = id;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.payment = payment;
        this.status = status;
        this.items = items;
        this.date = date;
    }

    public TransactionModel() {
    }

    public static TransactionModel fromDomain(Transaction transaction) {
        return new TransactionModel(
                transaction.getId(),
                transaction.getSellerId(),
                transaction.getBuyerId(),
                transaction.getPayment(),
                transaction.getStatus(),
                transaction.getItems().stream()
                        .map(OrderItemModel::fromDomain)
                        .toList(),
                transaction.getDate()
        );
    }

    public Transaction toDomain() {
        return Transaction.with(
                id,
                sellerId,
                buyerId,
                payment,
                status,
                items.stream()
                        .map(OrderItemModel::toDomain)
                        .toList(),
                date
        );
    }
}
