package org.pdv.service.transaction;

import org.pdv.domain.transaction.Payment;

import java.util.List;

public record TransactionOutput(
        String id,
        TransactionUserOutput seller,
        TransactionUserOutput buyer,
        Payment payment,
        List<OrderItemOutput> items,
        Integer totalQuantity,
        Double totalPrice
) {
}
