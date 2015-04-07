package com.jonathanfinerty.liquid.services;

import android.content.Intent;
import android.net.Uri;

import com.jonathanfinerty.liquid.persistence.ExpenseContract;

import timber.log.Timber;

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
            Timber.e("Expense Id long extra not set" + expenseId + " not set or valid");
            return false;
        }

        if (expenseId <= 0) {
            Timber.e("Expense Id long extra: " + expenseId + " not valid");
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
