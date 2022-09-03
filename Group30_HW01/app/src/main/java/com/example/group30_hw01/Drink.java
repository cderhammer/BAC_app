
/*
    Homework 01

    Drink.java

    Group:
    Matthew Carroll
    Conor Derhammer
 */

package com.example.group30_hw01;

public class Drink {

    private int drinkSize;
    private int percent;

    public Drink(int drinkSize, int percent){
        this.drinkSize = drinkSize;
        this.percent = percent;
    }

    public double getAlcoholContent(){
        return drinkSize * (percent / 100.0);
    }

    @Override
    public String toString(){
        return drinkSize + " oz, " + percent + "%";
    }

}
