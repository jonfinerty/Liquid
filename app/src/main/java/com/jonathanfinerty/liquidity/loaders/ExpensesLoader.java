package com.jonathanfinerty.liquidity.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.jonathanfinerty.liquidity.domain.Expense;
import com.jonathanfinerty.liquidity.persistence.ExpenseContract;
import com.jonathanfinerty.liquidity.presentation.viewmodel.ExpenseViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ExpensesLoader extends AsyncTaskLoader<ArrayList<ExpenseViewModel>> {

    private ExpensesContentObserver expensesContentObserver;

    private ArrayList<ExpenseViewModel> expenseViewModels;

    public ExpensesLoader(Context context) {
        super(context);
    }



    @Override
    protected void onStartLoading() {

        //todo: find out proper lifecycle of the loader and work out when this should be create and registered
        if (expensesContentObserver != null) {
            expensesContentObserver = new ExpensesContentObserver(new Handler());
            getContext().getContentResolver().registerContentObserver(ExpenseContract.GROUP_URI, true, expensesContentObserver);
        }

        if (expenseViewModels != null) {
            deliverResult(expenseViewModels);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public ArrayList<ExpenseViewModel> loadInBackground() {

        ArrayList<ExpenseViewModel> expenseVMs = new ArrayList<ExpenseViewModel>();

        List<Expense> expenses = getAll();

        for (Expense expense : expenses) {
            expenseVMs.add(new ExpenseViewModel(expense));
        }

        return expenseVMs;
    }

    @Override
    public void deliverResult(ArrayList<ExpenseViewModel> data) {
        expenseViewModels = data;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        expenseViewModels = null;
        getContext().getContentResolver().unregisterContentObserver(expensesContentObserver);
    }

    @Override
    public void onCanceled(ArrayList<ExpenseViewModel> data) {
        super.onCanceled(data);
        expenseViewModels = null;
    }

    public List<Expense> getAll() {

        Uri expensesUri = ExpenseContract.GROUP_URI;

        Cursor expenseCursor = getContext().getContentResolver().query(
                expensesUri,
                new String[]{
                        ExpenseContract._ID,
                        ExpenseContract.COLUMN_NAME_VALUE,
                        ExpenseContract.COLUMN_NAME_TIME
                },
                null,
                null,
                null
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

    public final class ExpensesContentObserver extends ContentObserver {

        public ExpensesContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            forceLoad();
        }
    }
}
