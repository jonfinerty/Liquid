package com.jonathanfinerty.liquid.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.jonathanfinerty.liquid.persistence.ExpenseContract;
import com.jonathanfinerty.liquid.persistence.ExpenseRepository;
import com.jonathanfinerty.liquid.presentation.viewmodel.ExpenseViewModel;

import timber.log.Timber;

public class ExpenseViewModelLoader extends AsyncTaskLoader<ExpenseViewModel>
                                    implements CallbackContentObserver.ChangeObserver {

    private CallbackContentObserver callbackContentObserver;

    private ExpenseViewModel cachedExpenseViewModel;
    private final long expenseId;

    public ExpenseViewModelLoader(Context context, long expenseId) {
        super(context);
        this.expenseId = expenseId;
    }

    @Override
    protected void onStartLoading() {

        Timber.d("Starting loading");

        //todo: find out proper lifecycle of the loader and work out when this should be create and registered
        if (callbackContentObserver == null) {

            Uri expenseUri = ExpenseContract.getSingleUri(expenseId);

            callbackContentObserver = new CallbackContentObserver(new Handler(), this);

            getContext().getContentResolver().registerContentObserver(
                    expenseUri,
                    true,
                    callbackContentObserver
            );

            Timber.d("Registered as content observer for uri: " + expenseUri);
        }

        if (cachedExpenseViewModel != null) {
            deliverResult(cachedExpenseViewModel);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public ExpenseViewModel loadInBackground() {

        ExpenseRepository expenseRepository = new ExpenseRepository(getContext());

        return expenseRepository.get(expenseId);
    }

    @Override
    public void deliverResult(ExpenseViewModel data) {
        cachedExpenseViewModel = data;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        cachedExpenseViewModel = null;
        getContext().getContentResolver().unregisterContentObserver(callbackContentObserver);
    }

    @Override
    public void onCanceled(ExpenseViewModel data) {
        super.onCanceled(data);
        cachedExpenseViewModel = null;
    }

    @Override
    public void onChange() {
        forceLoad();
    }
}
