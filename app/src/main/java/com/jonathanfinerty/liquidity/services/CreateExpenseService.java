package com.jonathanfinerty.liquidity.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.jonathanfinerty.liquidity.persistence.ExpenseContract;

public class CreateExpenseService extends BaseService {

    public static String VALUE_EXTRA = "value";
    public static String TIME_EXTRA = "time";

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
            Log.e("CreateExpenseOperation", "Value int extra: " + value + " not valid");
            return false;
        }

        long defaultTime = -1;
        time = intent.getLongExtra(TIME_EXTRA, defaultTime);

        if (time == defaultTime) {
            Log.e("CreateExpenseOperation", "Time long extra: " + value + " not valid");
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
    }
}
