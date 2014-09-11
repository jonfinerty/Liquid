package com.jonathanfinerty.liquidity.presentation.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.services.CreateExpenseService;
import com.jonathanfinerty.liquidity.presentation.fragments.EnterMoneyFragment;

import java.util.Date;

public class AddExpenseActivity extends Activity
                                implements EnterMoneyFragment.CurrencyEnteredListener {

    public static final String CLOSE_AFTER_ADD = "close once done";
    private boolean returnToHomeScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(EnterMoneyFragment.FRAGMENT_TITLE, "Enter Expense Amount");

        EnterMoneyFragment fragment = new EnterMoneyFragment();
        fragment.setArguments(fragmentArguments);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.linearlayout_add_expense, fragment).commit();

        returnToHomeScreen = getIntent().getBooleanExtra(CLOSE_AFTER_ADD, false);
    }

    @Override
    public void onCurrencyEntered(int amount) {

        Intent createExpense = new Intent(this, CreateExpenseService.class);

        createExpense.putExtra(CreateExpenseService.VALUE_EXTRA, amount);
        createExpense.putExtra(CreateExpenseService.TIME_EXTRA, new Date().getTime());

        this.startService(createExpense);

        if (returnToHomeScreen) {
            Intent startHomeScreen = new Intent(Intent.ACTION_MAIN);
            startHomeScreen.addCategory(Intent.CATEGORY_HOME);
            startHomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startHomeScreen);
        } else{
            finish();
        }
    }
}