package com.jonathanfinerty.liquidity.domain;

import android.content.ContentUris;
import android.net.Uri;
import android.text.format.DateUtils;

import com.jonathanfinerty.liquidity.persistence.LiquidityContract;

import java.util.Calendar;

public class Expense {

    private long id;

    private int value;

    private long time;

    public Expense(long id, int value, long time){
        this.id = id;
        this.value = value;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public String getHumanReadableValue() {
        float decimalisedTotal = ((float) value) / 100f;
        return "£" + String.format("%.2f", decimalisedTotal);
    }

    public CharSequence getHumanReadableTime() {
        return DateUtils.getRelativeTimeSpanString(time, Calendar.getInstance().getTimeInMillis(), DateUtils.SECOND_IN_MILLIS, 0 );
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

    public Uri getContentUri() {
        return ContentUris.withAppendedId(LiquidityContract.Expense.CONTENT_URI, id);
    }
}
