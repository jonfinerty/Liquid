package com.jonathanfinerty.liquidity.domain;

public class Expense {

    private long id;

    private int value;

    // ToDo: This should be a date time, the fact that it serialises into the db as a long is not a concern of the domain.
    private long time;

    public Expense(long id, int value, long time){
        this.id = id;
        this.value = value;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }

        if (this.getClass() != other.getClass()) {
            return false;
        }

        if (this.id != ((Expense) other).id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int)(id ^ (id >>> 32));
    }
}
