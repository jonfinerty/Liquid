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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);

        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(EnterMoneyFragment.FRAGMENT_TITLE, "Enter Expense Amount");

        EnterMoneyFragment fragment = new EnterMoneyFragment();
        fragment.setArguments(fragmentArguments);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.linearLayout_fragment_holder, fragment).commit();
    }

    @Override
    public void onCurrencyEntered(int amount) {

        Intent createExpense = new Intent(this, CreateExpenseService.class);

        createExpense.putExtra(CreateExpenseService.VALUE_EXTRA, amount);
        createExpense.putExtra(CreateExpenseService.TIME_EXTRA, new Date().getTime());

        this.startService(createExpense);

        finish();
    }
}