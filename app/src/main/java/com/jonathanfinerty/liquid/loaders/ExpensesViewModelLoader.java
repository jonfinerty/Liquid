package com.jonathanfinerty.liquid.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Handler;

import com.jonathanfinerty.liquid.domain.Expense;
import com.jonathanfinerty.liquid.persistence.ExpenseContract;
import com.jonathanfinerty.liquid.persistence.ExpenseRepository;
import com.jonathanfinerty.liquid.presentation.viewmodel.ExpenseViewModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ExpensesViewModelLoader extends AsyncTaskLoader<ArrayList<ExpenseViewModel>>
                                     implements CallbackContentObserver.ChangeObserver {

    private CallbackContentObserver callbackContentObserver;

    private ArrayList<ExpenseViewModel> cachedExpenseViewModels;

    public ExpensesViewModelLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {

        Timber.d("Starting loading");

        //todo: find out proper lifecycle of the loader and work out when this should be create and registered
        if (callbackContentObserver == null) {
            callbackContentObserver = new CallbackContentObserver(new Handler(), this);
            getContext().getContentResolver().registerContentObserver(ExpenseContract.GROUP_URI, true, callbackContentObserver);
            Timber.d("Registered as content observer for uri: " + ExpenseContract.GROUP_URI);
        }

        if (cachedExpenseViewModels != null) {
            deliverResult(cachedExpenseViewModels);
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

        ExpenseRepository expenseRepository = new ExpenseRepository(getContext());

        List<Expense> expenses = expenseRepository.getAll();

        ArrayList<ExpenseViewModel> expenseViewModels = new ArrayList<ExpenseViewModel>();

        for (Expense expense : expenses) {
            expenseViewModels.add(new ExpenseViewModel(expense));
        }

        return expenseViewModels;
    }

    @Override
    public void deliverResult(ArrayList<ExpenseViewModel> data) {
        cachedExpenseViewModels = data;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        cachedExpenseViewModels = null;
        getContext().getContentResolver().unregisterContentObserver(callbackContentObserver);
    }

    @Override
    public void onCanceled(ArrayList<ExpenseViewModel> data) {
        super.onCanceled(data);
        cachedExpenseViewModels = null;
    }

    @Override
    public void onChange() {
        forceLoad();
    }
}
