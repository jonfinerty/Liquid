package com.jonathanfinerty.liquidity.persistence;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.jonathanfinerty.liquidity.domain.Expense;

import java.util.ArrayList;

public class ExpenseRepository {

    private Context context;

    public ExpenseRepository(Context context) {
        this.context = context;
    }

    public ArrayList<Expense> getAll() {

        ArrayList<Expense> expenses = new ArrayList<Expense>();

        Uri expensesUri = ExpenseContract.GROUP_URI;

        Cursor expenseCursor = context.getContentResolver().query(
            expensesUri,
            new String[]{
                    ExpenseContract._ID,
                    ExpenseContract.COLUMN_NAME_VALUE,
                    ExpenseContract.COLUMN_NAME_TIME
            },
            null,
            null,
            ExpenseContract.DEFAULT_SORT_ORDER
        );

        expenseCursor.moveToFirst();

        int idColumnIndex = expenseCursor.getColumnIndex(ExpenseContract._ID);
        int valueColumnIndex = expenseCursor.getColumnIndex(ExpenseContract.COLUMN_NAME_VALUE);
        int timeColumnIndex = expenseCursor.getColumnIndex(ExpenseContract.COLUMN_NAME_TIME);

        while (!expenseCursor.isAfterLast()) {

            long id = expenseCursor.getLong(idColumnIndex);
            int value = expenseCursor.getInt(valueColumnIndex);
            long time = expenseCursor.getLong(timeColumnIndex);

            Expense expense = new Expense(id, value, time);

            expenses.add(expense);

            expenseCursor.moveToNext();
        }

        expenseCursor.close();

        return expenses;
    }
}
