package com.jonathanfinerty.liquidity.persistence;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.jonathanfinerty.liquidity.domain.Budget;
import com.jonathanfinerty.liquidity.domain.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ExpenseRepository {

    private Context context;

    public ExpenseRepository(Context context) {
        this.context = context;
    }

    public ArrayList<Expense> getAll() {

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

        return getExpensesFromCursor(expenseCursor);

    }

    private ArrayList<Expense> getExpensesFromCursor(Cursor expenseCursor) {
        ArrayList<Expense> expenses = new ArrayList<Expense>();

        expenseCursor.moveToFirst();

        int idColumnIndex = expenseCursor.getColumnIndex(ExpenseContract._ID);
        int valueColumnIndex = expenseCursor.getColumnIndex(ExpenseContract.COLUMN_NAME_VALUE);
        int timeColumnIndex = expenseCursor.getColumnIndex(ExpenseContract.COLUMN_NAME_TIME);

        while (!expenseCursor.isAfterLast()) {

            long id = expenseCursor.getLong(idColumnIndex);
            int value = expenseCursor.getInt(valueColumnIndex);
            long time = expenseCursor.getLong(timeColumnIndex);

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTimeInMillis(time);

            Expense expense = new Expense(id, value, calendar);

            expenses.add(expense);

            expenseCursor.moveToNext();
        }

        expenseCursor.close();

        return expenses;
    }

    public ArrayList<Expense> getForBudgetPeriod(Budget budget){

        Calendar calendarToday = Calendar.getInstance();
        int currentDay = calendarToday.get(Calendar.DAY_OF_MONTH);

        Calendar budgetStartTime = Calendar.getInstance();

        if (currentDay < budget.getDate()) {
            // get last months budget day
            budgetStartTime.add(Calendar.MONTH, -1);
        }

        budgetStartTime.set(Calendar.DAY_OF_MONTH, budget.getDate());
        budgetStartTime.set(Calendar.HOUR_OF_DAY, 0);
        budgetStartTime.set(Calendar.MINUTE, 0);
        budgetStartTime.set(Calendar.SECOND, 0);
        budgetStartTime.set(Calendar.MILLISECOND, 0);

        Uri expensesUri = ExpenseContract.GROUP_URI;

        Cursor expenseCursor = context.getContentResolver().query(
                expensesUri,
                new String[]{
                        ExpenseContract._ID,
                        ExpenseContract.COLUMN_NAME_VALUE,
                        ExpenseContract.COLUMN_NAME_TIME
                },
                ExpenseContract.COLUMN_NAME_TIME + " > " + budgetStartTime.getTimeInMillis(),
                null,
                ExpenseContract.DEFAULT_SORT_ORDER);

        return getExpensesFromCursor(expenseCursor);
    }
}
