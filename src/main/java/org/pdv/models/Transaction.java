package org.pdv.models;

import java.util.UUID;

public class Transaction {
    private UUID id;
    private Product product;
    private int quantity;
    private int paymentMethod;
    private User buyer;
    private User seller;

    public Transaction(Product product, int quantity, User buyer, User seller) {
        this.id = UUID.randomUUID();
        this.product = product;
        this.quantity = quantity;
        this.buyer = buyer;
        this.seller = seller;
    }

    public double getTotal() {
        return this.product.getValue() * this.quantity;
    }

    public String getId() {
        return this.id.toString();
    }

    public void printTransaction() {
        String productName = this.product.getName();
        double productValue = this.product.getValue();
        double total = this.getTotal();
        String buyerName = this.buyer.getName();
        String sellerName = this.seller.getName();

        String message = String.format("""
                        -- Transação --
                         Produto: %s
                         Valor: %.2f
                         Quantidade: %d
                         Total: %.2f
                         Comprador: %s
                         Vendedor: %s
                        ----------------
                        """, productName,productValue, this.quantity, total,  buyerName, sellerName );

        System.out.println(message);
    }
}
