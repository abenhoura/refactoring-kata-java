package com.sipios.refactoring.model;

public enum Product {
    TSHIRT(30,1),
    DRESS(50,.8),
    JACKET(100,.9);

    private double price;
    private double discount;

    Product(double price, double discount){
        this.price = price;
        this.discount = discount;
    }

    public double getPrice(){
        return price;
    }
    public double getDiscount(){
        return discount;
    }

}
