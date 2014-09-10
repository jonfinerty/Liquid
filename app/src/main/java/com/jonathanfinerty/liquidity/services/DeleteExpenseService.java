package com.jonathanfinerty.liquidity.services;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.jonathanfinerty.liquidity.persistence.ExpenseContract;

public class DeleteExpenseService extends BaseService {

    public final static String EXPENSE_ID_EXTRA = "expense id";

    private long expenseId;

    public DeleteExpenseService() {
        super("Delete Expense Operation");
    }

    @Override
    protected boolean ValidateIntentExtras(Intent intent) {

        long defaultExpenseId = -1;
        expenseId = intent.getLongExtra(EXPENSE_ID_EXTRA, defaultExpenseId);

        if (expenseId == defaultExpenseId) {
            Log.e("DeleteExpenseOperation", "Expense Id long extra not set" + expenseId + " not set or valid");
            return false;
        }

        if (expenseId <= 0) {
            Log.e("DeleteExpenseOperation", "Expense Id long extra: " + expenseId + " not valid");
            return false;
        }

        return true;
    }

    @Override
    protected void ExecuteOperation() {

        Uri expenseToDeleteUri = ExpenseContract.getSingleUri(expenseId);
        this.getContentResolver().delete(expenseToDeleteUri, null, null);

    }
}
