package com.jonathanfinerty.liquidity.presentation.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.domain.Budget; //todo: use of the domain is a bit suspicious
import com.jonathanfinerty.liquidity.services.SetBudgetService;
import com.jonathanfinerty.liquidity.persistence.BudgetRepository;
import com.jonathanfinerty.liquidity.presentation.fragments.EnterDateFragment;
import com.jonathanfinerty.liquidity.presentation.fragments.EnterMoneyFragment;

public class SetBudgetActivity extends FragmentActivity
                               implements EnterDateFragment.DateEnteredListener,
                                          EnterMoneyFragment.CurrencyEnteredListener

{

    private Budget budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);

        BudgetRepository budgetRepository = new BudgetRepository(this);
        budget = budgetRepository.get();

        EnterDateFragment enterDateFragment = new EnterDateFragment();

        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putInt(EnterDateFragment.DEFAULT_VALUE, budget.getDate());
        enterDateFragment.setArguments(fragmentArguments);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linearlayout_fragment_holder, enterDateFragment).commit();
    }

    @Override
    public void onDateEntered(int date) {

        Intent setBudget = new Intent(this, SetBudgetService.class);
        setBudget.putExtra(SetBudgetService.DATE_EXTRA, date);
        this.startService(setBudget);

        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(EnterMoneyFragment.FRAGMENT_TITLE, "Enter Budget Amount");
        fragmentArguments.putInt(EnterMoneyFragment.DEFAULT_VALUE, budget.getAmount());

        EnterMoneyFragment enterMoneyFragment = new EnterMoneyFragment();
        enterMoneyFragment.setArguments(fragmentArguments);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        fragmentTransaction.replace(R.id.linearlayout_fragment_holder, enterMoneyFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onCurrencyEntered(int amount) {

        Intent setBudget = new Intent(this, SetBudgetService.class);
        setBudget.putExtra(SetBudgetService.BUDGET_AMOUNT_EXTRA, amount);
        this.startService(setBudget);

        finish();
    }

}
