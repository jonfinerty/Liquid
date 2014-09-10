package com.jonathanfinerty.liquidity.presentation.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.domain.Budget;
import com.jonathanfinerty.liquidity.operations.SetBudgetOperation;
import com.jonathanfinerty.liquidity.persistence.BudgetRepository;
import com.jonathanfinerty.liquidity.presentation.fragments.EnterDateFragment;
import com.jonathanfinerty.liquidity.presentation.fragments.EnterMoneyFragment;

public class SetBudgetActivity extends FragmentActivity
                               implements EnterDateFragment.DateEnteredListener,
                                    EnterMoneyFragment.CurrencyEnteredListener

{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_budget);

        BudgetRepository budgetRepository = new BudgetRepository(this);
        Budget budget = budgetRepository.get();

        EnterDateFragment enterDateFragment = new EnterDateFragment();

        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putInt(EnterDateFragment.DEFAULT_VALUE, budget.getDate());
        enterDateFragment.setArguments(fragmentArguments);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linearlayout_set_budget, enterDateFragment).commit();
    }

    @Override
    public void onDateEntered(int date) {

        Intent setBudget = new Intent(this, SetBudgetOperation.class);
        setBudget.putExtra(SetBudgetOperation.DATE_EXTRA, date);
        this.startService(setBudget);

        BudgetRepository budgetRepository = new BudgetRepository(this);
        Budget budget = budgetRepository.get();

        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(EnterMoneyFragment.FRAGMENT_TITLE, "Enter Budget Amount");
        fragmentArguments.putInt(EnterMoneyFragment.DEFAULT_VALUE, budget.getAmount());

        EnterMoneyFragment enterMoneyFragment = new EnterMoneyFragment();
        enterMoneyFragment.setArguments(fragmentArguments);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        fragmentTransaction.replace(R.id.linearlayout_set_budget, enterMoneyFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onCurrencyEntered(int amount) {

        Intent setBudget = new Intent(this, SetBudgetOperation.class);
        setBudget.putExtra(SetBudgetOperation.BUDGET_AMOUNT_EXTRA, amount);
        this.startService(setBudget);

        finish();
    }

}
