package com.jonathanfinerty.liquidity.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.jonathanfinerty.liquidity.domain.Budget;
import com.jonathanfinerty.liquidity.domain.Expense;
import com.jonathanfinerty.liquidity.persistence.BudgetContract;
import com.jonathanfinerty.liquidity.persistence.BudgetRepository;
import com.jonathanfinerty.liquidity.persistence.ExpenseContract;
import com.jonathanfinerty.liquidity.persistence.ExpenseRepository;
import com.jonathanfinerty.liquidity.presentation.viewmodel.BudgetTankViewModel;

import java.util.ArrayList;

public class BudgetTankViewModelLoader extends AsyncTaskLoader<BudgetTankViewModel>
                                       implements CallbackContentObserver.ChangeObserver {

    private static final String TAG = "BudgetTankViewModel Loader";
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

        BudgetTankViewModel budgetTankViewModel = new BudgetTankViewModel(budget, totalSpent);

        return budgetTankViewModel;
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
