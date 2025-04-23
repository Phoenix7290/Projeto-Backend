package org.pdv.repository.transaction.models;

import org.pdv.domain.transaction.OrderItem;

public class OrderItemModel {
    public String id;
    public String productId;
    public Integer quantity;
    public Double price;

    public OrderItemModel(String id, String productId, Integer quantity, Double price) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItemModel() {
    }

    public static OrderItemModel fromDomain(OrderItem orderItem) {
        return new OrderItemModel(
                orderItem.getId(),
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }

    public OrderItem toDomain() {
        return OrderItem.with(
                id,
                productId,
                quantity,
                price
        );
    }
}
