package org.pdv.domain.transaction;

import org.pdv.domain.error.DomainException;
import org.pdv.domain.utils.IdUtils;
import org.pdv.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private final String id;
    private String sellerId;
    private String buyerId;
    private Payment payment;
    private Status status;
    private List<OrderItem> items;
    private Instant date;

    private Transaction(
            String id,
            String sellerId,
            String buyerId,
            Payment payment,
            Status status,
            List<OrderItem> items,
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

    public static Transaction with(
            String id,
            String sellerId,
            String buyerId,
            Payment payment,
            Status status,
            List<OrderItem> items,
            Instant date
    ) {
        return new Transaction(
                id,
                sellerId,
                buyerId,
                payment,
                status,
                items,
                date
        );
    }

    public static Transaction with(
            String sellerId,
            String buyerId,
            Payment payment,
            List<OrderItem> items
    ) {
        return new Transaction(
                IdUtils.uuid(),
                sellerId,
                buyerId,
                payment,
                Status.COMPLETED,
                items,
                InstantUtils.now()
        );
    }

    public String getId() {
        return id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public Payment getPayment() {
        return payment;
    }

    public Status getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Instant getDate() {
        return date;
    }

    public Integer getTotalQuantity() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public Double getTotalPrice() {
        return items.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();
    }

    public void validate() throws DomainException {
        List<Error> errors = new ArrayList<>();
        if (sellerId == null || sellerId.isBlank()) {
            errors.add(new Error("'sellerId' should not be empty"));
        }
        if (buyerId == null || buyerId.isBlank()) {
            errors.add(new Error("'buyerId' should not be empty"));
        }
        if (items == null || items.isEmpty()) {
            errors.add(new Error("'items' should not be empty"));
        }

        if (!errors.isEmpty()) {
            throw DomainException.with(errors);
        }
    }
}
