package com.jonathanfinerty.liquid.persistence;

import android.content.Context;
import android.database.Cursor;

import com.jonathanfinerty.liquid.domain.Budget;

public class BudgetRepository {
    private final Context context;

    public BudgetRepository(Context context) {
        this.context = context;
    }

    public Budget get() {

        Budget budget;

        Cursor budgetCursor = context.getContentResolver().query(
                BudgetContract.URI,
                new String[]{
                        BudgetContract.COLUMN_NAME_AMOUNT,
                        BudgetContract.COLUMN_NAME_DATE
                },
                null,
                null,
                null
        );

        budgetCursor.moveToFirst();

        int amountColumnIndex = budgetCursor.getColumnIndex(BudgetContract.COLUMN_NAME_AMOUNT);
        int dateColumnIndex = budgetCursor.getColumnIndex(BudgetContract.COLUMN_NAME_DATE);

        int amount = budgetCursor.getInt(amountColumnIndex);
        int date = budgetCursor.getInt(dateColumnIndex);

        budget = new Budget(amount, date);

        budgetCursor.close();

        return budget;
    }
}
