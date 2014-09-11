package com.jonathanfinerty.liquidity.presentation.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.presentation.fragments.BudgetFragment;
import com.jonathanfinerty.liquidity.presentation.fragments.ListExpenseFragment;

public class OverviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager_overview);
        viewpager.setAdapter(new LiquidityPagerAdapter(getFragmentManager()));

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagertabstrip_overview);
        pagerTabStrip.setTextColor(getResources().getColor(R.color.blue_dark));
        pagerTabStrip.setTabIndicatorColorResource(R.color.blue_dark);
        pagerTabStrip.setDrawFullUnderline(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_set_budget: {
                Intent setBudgetActivity = new Intent(this, SetBudgetActivity.class);
                startActivity(setBudgetActivity);
                return true;
            }
            case R.id.action_add_expense: {
                Intent addExpenseIntent = new Intent(this, AddExpenseActivity.class);
                startActivity(addExpenseIntent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void addExpense(View view) {
        Intent addExpenseIntent = new Intent(this, AddExpenseActivity.class);
        startActivity(addExpenseIntent);
    }

    public static class LiquidityPagerAdapter extends FragmentPagerAdapter {

        public LiquidityPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: {
                    return new BudgetFragment();
                }
                case 1: {
                    return new ListExpenseFragment();
                }
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: {
                    return "Budget";
                }
                case 1: {
                    return "Expenses";
                }
            }

            return null;
        }
    }
}
