package com.sipios.refactoring.model;


public enum CustomerType {
    STANDARD_CUSTOMER(1.0,200, "standard customer"),
    PREMIUM_CUSTOMER(.9,800, "premium customer"),
    PLATINUM_CUSTOMER(.5,2000, "platinum customer");

    public String getType() {
        return type;
    }

    private double factor;
    private final double threshold;
    private final String type;


    CustomerType(double factor, double threshold, String type){
        this.factor = factor;
        this.threshold = threshold;
        this.type = type;
    }

    public double getFactor(){
        return factor;
    }

    public double getThreshold() {
        return threshold;
    }
}
