package com.jonathanfinerty.liquidity.presentation.viewmodel;

import android.text.format.DateUtils;

import com.jonathanfinerty.liquidity.domain.Expense;

import java.util.Calendar;

public class ExpenseViewModel implements Comparable<ExpenseViewModel> {

    private int value;

    // todo: this shouldn't be a long that again is a concern of the persistence layer
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

    @Override
    public int compareTo(ExpenseViewModel expenseViewModel) {
        return Long.compare(time, expenseViewModel.time);
    }
}
