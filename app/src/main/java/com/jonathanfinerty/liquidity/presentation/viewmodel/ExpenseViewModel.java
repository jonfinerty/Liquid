package com.jonathanfinerty.liquidity.presentation.viewmodel;

import android.text.format.DateUtils;

import com.jonathanfinerty.liquidity.domain.Expense;

import java.util.Calendar;

public class ExpenseViewModel {

    private int value;
    private long time;

    public ExpenseViewModel(Expense expense) {
        value = expense.getValue();
        time = expense.getTime();
    }

    public String getHumanReadableValue() {
        float decimalisedTotal = ((float) value) / 100f;
        return "£" + String.format("%.2f", decimalisedTotal);
    }

    public CharSequence getHumanReadableTime() {
        return DateUtils.getRelativeTimeSpanString(time, Calendar.getInstance().getTimeInMillis(), DateUtils.SECOND_IN_MILLIS, 0);
    }
}
