package com.jonathanfinerty.liquidity;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.jonathanfinerty.liquidity.ContentProvider.LiquidityContract;

import java.util.Date;

public class AddExpenseActivity extends FragmentActivity
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
        Uri expenseUri = LiquidityContract.Expense.CONTENT_URI;
        ContentValues expenseValues = new ContentValues();
        expenseValues.put(LiquidityContract.Expense.COLUMN_NAME_VALUE, amount);
        expenseValues.put(LiquidityContract.Expense.COLUMN_NAME_TIME, new Date().getTime());

        getContentResolver().insert(expenseUri, expenseValues);

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