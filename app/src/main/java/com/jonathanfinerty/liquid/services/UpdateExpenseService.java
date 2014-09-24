package com.jonathanfinerty.liquid.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.jonathanfinerty.liquid.persistence.ExpenseContract;

public class UpdateExpenseService extends BaseService {

    private static final String TAG = "Update Expense Service";

    public static final String EXPENSE_ID_EXTRA = "expense id";
    public static final String NEW_AMOUNT_EXTRA = "new amount";
    public static final String NEW_DATE_EXTRA = "new date";

    private long expenseId;
    private int newAmount;
    private long newDate;

    public UpdateExpenseService() {
        super(TAG);
    }

    @Override
    protected boolean ValidateIntentExtras(Intent intent) {

        long defaultExpenseId = -1;
        expenseId = intent.getLongExtra(EXPENSE_ID_EXTRA, defaultExpenseId);

        int defaultNewAmount = -1;
        newAmount = intent.getIntExtra(NEW_AMOUNT_EXTRA, defaultNewAmount);

        long defaultNewDate = -1;
        newDate = intent.getLongExtra(NEW_DATE_EXTRA, defaultNewDate);

        if (expenseId <= 0) {
            Log.e(TAG, "Expense id was not set");
            return false;
        }

        if (newAmount <= 0 && newDate <= 0) {
            Log.e(TAG, "new date and new amount were not set");
            return false;
        }

        return true;
    }

    @Override
    protected void ExecuteOperation() {
        Uri expenseUri = ExpenseContract.getSingleUri(expenseId);

        ContentValues budgetValues = new ContentValues();

        if (newAmount >= 0) {
            budgetValues.put(ExpenseContract.COLUMN_NAME_VALUE, newAmount);
        }

        if (newDate >= 0) {
            budgetValues.put(ExpenseContract.COLUMN_NAME_TIME, newDate);
        }

        this.getContentResolver().update(expenseUri, budgetValues, null, null);
    }
}
