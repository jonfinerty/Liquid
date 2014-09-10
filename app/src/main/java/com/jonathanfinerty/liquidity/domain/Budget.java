package com.jonathanfinerty.liquidity.domain;

public class Budget {

    private int amount;

    private int start_day_of_month;

    public Budget(int amount, int start_day_of_month) {
        this.amount = amount;
        this.start_day_of_month = start_day_of_month;
    }

    public int getAmount() {
        return amount;
    }

    public int getStart_day_of_month() {
        return start_day_of_month;
    }
}
