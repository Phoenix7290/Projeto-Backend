package org.pdv.domain.transaction;

import org.pdv.domain.utils.IdUtils;

public class OrderItem {
    private final String id;
    private String productId;
    private Integer quantity;
    private Double price;

    private OrderItem(String id, String productId, int quantity, double price) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItem with(String id, String productId, int quantity, double price) {
        return new OrderItem(id, productId, quantity, price);
    }

    public static OrderItem with(String productId, int quantity, double price) {
        return new OrderItem(IdUtils.uuid(), productId, quantity, price);
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }
}
