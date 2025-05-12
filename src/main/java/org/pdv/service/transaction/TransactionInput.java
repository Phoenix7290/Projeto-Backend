package org.pdv.service.transaction;

import org.pdv.domain.transaction.Payment;

import java.util.List;

public record TransactionInput(
        String sellerId,
        String buyerId,
        Payment payment,
        List<OrderItemInput> items
) {
}
