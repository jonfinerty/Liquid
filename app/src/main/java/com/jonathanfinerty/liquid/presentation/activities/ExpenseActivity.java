package com.jonathanfinerty.liquid.presentation.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import com.jonathanfinerty.liquid.R;
import com.jonathanfinerty.liquid.persistence.ExpenseRepository;
import com.jonathanfinerty.liquid.presentation.fragments.EnterMoneyFragment;
import com.jonathanfinerty.liquid.presentation.fragments.ExpenseDetailsFragment;
import com.jonathanfinerty.liquid.presentation.viewmodel.ExpenseViewModel;
import com.jonathanfinerty.liquid.services.DeleteExpenseService;
import com.jonathanfinerty.liquid.services.UpdateExpenseService;

import java.util.Calendar;

import timber.log.Timber;

public class ExpenseActivity extends Activity
                             implements ExpenseDetailsFragment.ExpenseDetailsClickListener,
                                        EnterMoneyFragment.CurrencyEnteredListener,
                                        DatePickerDialog.OnDateSetListener {

    public static final String EXPENSE_ID_EXTRA = "expense id";

    private ExpenseViewModel expenseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);

        Intent intent = getIntent();
        long defaultExtraValue = -1;
        long expenseId = intent.getLongExtra(EXPENSE_ID_EXTRA, defaultExtraValue);

        if (expenseId == defaultExtraValue) {
            Timber.d("No expense id passed in intent, finishing.");
            finish();
        }

        ExpenseRepository expenseRepository = new ExpenseRepository(this);
        expenseViewModel = expenseRepository.get(expenseId);

        if (expenseViewModel == null) {
            Timber.d("No expense found with id " + expenseId + ", finishing.");
            finish();
        }

        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putLong(ExpenseDetailsFragment.EXPENSE_ID_ARGUMENT, expenseViewModel.getId());

        ExpenseDetailsFragment fragment = new ExpenseDetailsFragment();
        fragment.setArguments(fragmentArguments);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.linearLayout_fragment_holder, fragment).commit();
    }

    @Override
    public void onEditAmountClicked() {
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(EnterMoneyFragment.FRAGMENT_TITLE, "Enter New Expense Amount");

        EnterMoneyFragment fragment = new EnterMoneyFragment();
        fragment.setArguments(fragmentArguments);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.linearLayout_fragment_holder, fragment).commit();
    }

    @Override
    public void onEditDateClicked() {
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(EnterMoneyFragment.FRAGMENT_TITLE, "Enter New Expense Amount");

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                expenseViewModel.getCalendar().get(Calendar.YEAR),
                expenseViewModel.getCalendar().get(Calendar.MONTH),
                expenseViewModel.getCalendar().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    public void onDeleteExpenseClicked() {
        Intent deleteExpense = new Intent(this, DeleteExpenseService.class);
        deleteExpense.putExtra(DeleteExpenseService.EXPENSE_ID_EXTRA, expenseViewModel.getId());
        startService(deleteExpense);

        finish();
    }

    @Override
    public void onCurrencyEntered(int amount) {

        Intent updateExpense = new Intent(this, UpdateExpenseService.class);
        updateExpense.putExtra(UpdateExpenseService.EXPENSE_ID_EXTRA, expenseViewModel.getId());
        updateExpense.putExtra(UpdateExpenseService.NEW_AMOUNT_EXTRA, amount);
        startService(updateExpense);

        getFragmentManager().popBackStack();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        Intent updateExpense = new Intent(this, UpdateExpenseService.class);
        updateExpense.putExtra(UpdateExpenseService.EXPENSE_ID_EXTRA, expenseViewModel.getId());
        updateExpense.putExtra(UpdateExpenseService.NEW_DATE_EXTRA, calendar.getTimeInMillis());
        startService(updateExpense);
    }
}
