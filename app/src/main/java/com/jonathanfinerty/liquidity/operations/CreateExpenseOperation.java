package com.jonathanfinerty.liquidity.operations;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.jonathanfinerty.liquidity.persistence.ExpenseContract;

public class CreateExpenseOperation implements Operation {

    private final int amount;
    private final long time;

    public CreateExpenseOperation(int amount, long time) {
        this.amount = amount;
        this.time = time;
    }

    @Override
    public void Execute(Context context) {

        Uri expenseUri = ExpenseContract.GROUP_URI;

        ContentValues expenseValues = new ContentValues();
        expenseValues.put(ExpenseContract.COLUMN_NAME_VALUE, amount);
        expenseValues.put(ExpenseContract.COLUMN_NAME_TIME, time);

        context.getContentResolver().insert(expenseUri, expenseValues);
    }
}
