package com.jonathanfinerty.liquid.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.jonathanfinerty.liquid.domain.Budget;
import com.jonathanfinerty.liquid.domain.Expense;
import com.jonathanfinerty.liquid.persistence.BudgetContract;
import com.jonathanfinerty.liquid.persistence.BudgetRepository;
import com.jonathanfinerty.liquid.persistence.ExpenseContract;
import com.jonathanfinerty.liquid.persistence.ExpenseRepository;
import com.jonathanfinerty.liquid.presentation.viewmodel.BudgetTankViewModel;

import java.util.ArrayList;

public class BudgetTankViewModelLoader extends AsyncTaskLoader<BudgetTankViewModel>
                                       implements CallbackContentObserver.ChangeObserver {

    private static final String TAG = "BudgetTankViewModelLoader";
    private CallbackContentObserver callbackContentObserver;

    private BudgetTankViewModel cachedBudgetTankViewModel;

    public BudgetTankViewModelLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "Starting loading");

        if (callbackContentObserver == null) {
            callbackContentObserver = new CallbackContentObserver(new Handler(), this);
            getContext().getContentResolver().registerContentObserver(BudgetContract.URI, true, callbackContentObserver);
            Log.d(TAG, "Registered as content observer for uri: " + BudgetContract.URI);
            getContext().getContentResolver().registerContentObserver(ExpenseContract.GROUP_URI, true, callbackContentObserver);
            Log.d(TAG, "Registered as content observer for uri: " + ExpenseContract.GROUP_URI);
        }

        if (cachedBudgetTankViewModel != null) {
            deliverResult(cachedBudgetTankViewModel);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public BudgetTankViewModel loadInBackground() {

        Context context = getContext();

        BudgetRepository budgetRepository = new BudgetRepository(context);

        Budget budget = budgetRepository.get();

        ExpenseRepository expenseRepository = new ExpenseRepository(context);

        ArrayList<Expense> currentBudgetExpenses = expenseRepository.getForBudgetPeriod(budget);

        int totalSpent = 0;

        for (Expense expense : currentBudgetExpenses) {
            totalSpent += expense.getValue();
        }

        return new BudgetTankViewModel(budget, totalSpent);
    }

    @Override
    public void deliverResult(BudgetTankViewModel data) {
        cachedBudgetTankViewModel = data;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        cachedBudgetTankViewModel = null;
        getContext().getContentResolver().unregisterContentObserver(callbackContentObserver);
    }

    @Override
    public void onCanceled(BudgetTankViewModel data) {
        super.onCanceled(data);
        cachedBudgetTankViewModel = null;
    }

    @Override
    public void onChange() {
        forceLoad();
    }
}
