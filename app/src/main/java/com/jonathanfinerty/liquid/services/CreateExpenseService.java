package com.jonathanfinerty.liquid.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import com.jonathanfinerty.liquid.persistence.ExpenseContract;

import timber.log.Timber;

public class CreateExpenseService extends BaseService {

    public static final String VALUE_EXTRA = "value";
    public static final String TIME_EXTRA = "time";

    private int value;
    private long time;

    public CreateExpenseService() {
        super("Create Expense Operation");
    }

    @Override
    protected boolean ValidateIntentExtras(Intent intent) {
        int defaultValue = -1;
        value = intent.getIntExtra(VALUE_EXTRA, defaultValue);

        if (value == defaultValue) {
            Timber.e("Value int extra: " + value + " not valid");
            return false;
        }

        long defaultTime = -1;
        time = intent.getLongExtra(TIME_EXTRA, defaultTime);

        if (time == defaultTime) {
            Timber.e("Time long extra: " + value + " not valid");
            return false;
        }

        return true;
    }

    @Override
    protected void ExecuteOperation() {
        Uri expenseUri = ExpenseContract.GROUP_URI;

        ContentValues expenseValues = new ContentValues();
        expenseValues.put(ExpenseContract.COLUMN_NAME_VALUE, value);
        expenseValues.put(ExpenseContract.COLUMN_NAME_TIME, time);

        this.getContentResolver().insert(expenseUri, expenseValues);
        Timber.d("Expense (Value: " + value + " Time: " + time + ") inserted");
    }
}
