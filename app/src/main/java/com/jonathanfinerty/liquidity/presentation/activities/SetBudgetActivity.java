package com.jonathanfinerty.liquidity.presentation.activities;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.domain.Budget;
import com.jonathanfinerty.liquidity.presentation.fragments.EnterDateFragment;
import com.jonathanfinerty.liquidity.presentation.fragments.EnterMoneyFragment;

public class SetBudgetActivity extends FragmentActivity
                               implements EnterDateFragment.DateEnteredListener,
        EnterMoneyFragment.CurrencyEnteredListener

{
    private final static int NOT_SET = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_budget);

        SharedPreferences settings = getSharedPreferences(Budget.PREFERENCES, 0);
        int currentBudgetDate = settings.getInt(Budget.DATE_PREFERENCE, NOT_SET);

        EnterDateFragment enterDateFragment = new EnterDateFragment();

        if (currentBudgetDate != NOT_SET){
            Bundle fragmentArguments = new Bundle();
            fragmentArguments.putInt(EnterDateFragment.DEFAULT_VALUE, currentBudgetDate);
            enterDateFragment.setArguments(fragmentArguments);
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linearlayout_set_budget, enterDateFragment).commit();
    }

    @Override
    public void onDateEntered(int date) {
        SharedPreferences settings = getSharedPreferences(Budget.PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Budget.DATE_PREFERENCE, date);
        editor.commit();

        int currentBudgetAmount = settings.getInt(Budget.AMOUNT_PREFERENCE, NOT_SET);

        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(EnterMoneyFragment.FRAGMENT_TITLE, "Enter Budget Amount");

        if (currentBudgetAmount != NOT_SET) {
            fragmentArguments.putInt(EnterMoneyFragment.DEFAULT_VALUE, currentBudgetAmount );
        }

        EnterMoneyFragment enterMoneyFragment = new EnterMoneyFragment();
        enterMoneyFragment.setArguments(fragmentArguments);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        fragmentTransaction.replace(R.id.linearlayout_set_budget, enterMoneyFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onCurrencyEntered(int amount) {
        SharedPreferences settings = getSharedPreferences(Budget.PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Budget.AMOUNT_PREFERENCE, amount);
        editor.commit();

        finish();
    }

}
