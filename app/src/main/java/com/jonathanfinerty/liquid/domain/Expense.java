package com.jonathanfinerty.liquid.domain;

import java.util.Calendar;

public class Expense {

    private final long id;

    private final int value;

    private final Calendar time;

    public Expense(long id, int value, Calendar time){
        this.id = id;
        this.value = value;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public Calendar getTime() {
        return time;
    }

    public int getValue() {
        return value;
    }
}
