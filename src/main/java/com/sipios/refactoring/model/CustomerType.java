package com.sipios.refactoring.model;

public enum CustomerType {
    STANDARD_CUSTOMER(1.0),
    PREMIUM_CUSTOMER(.9),
    PLATINUM_CUSTOMER(.5);
    private double factor;

    CustomerType(double factor){
        this.factor = factor;
    }

    public double getFactor(){
        return factor;
    }

}
