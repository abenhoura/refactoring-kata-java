package com.sipios.refactoring.model;

public enum Product {
    TSHIRT(30),
    DRESS(50),
    JACKET(100);

    private double price;

    Product(double price){
        this.price = price;
    }

    public double getPrice(){
        return price;
    }

}
