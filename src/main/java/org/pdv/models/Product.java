package org.pdv.models;

public class Product {
    private String name;
    private String type;
    private double value;

    public Product(String name, String type, double value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

}
