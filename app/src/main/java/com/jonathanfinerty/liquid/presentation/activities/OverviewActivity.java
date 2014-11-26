package com.jonathanfinerty.liquid.presentation.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jonathanfinerty.liquid.R;
import com.jonathanfinerty.liquid.presentation.fragments.BudgetFragment;
import com.jonathanfinerty.liquid.presentation.fragments.ListExpenseFragment;

public class OverviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager_overview);
        viewpager.setAdapter(new LiquidPagerAdapter(getFragmentManager(), this));

        Button button = (Button) findViewById(R.id.overview_activity_button_add_expense);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addExpenseIntent = new Intent(OverviewActivity.this, AddExpenseActivity.class);
                startActivity(addExpenseIntent);
            }
        });

        this.getActionBar().setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_set_budget:
                Intent setBudgetActivity = new Intent(this, SetBudgetActivity.class);
                startActivity(setBudgetActivity);
                return true;
            case R.id.action_add_expense:
                Intent addExpenseIntent = new Intent(this, AddExpenseActivity.class);
                startActivity(addExpenseIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static class LiquidPagerAdapter extends FragmentPagerAdapter  {

        private Context context;

        public LiquidPagerAdapter(FragmentManager fragmentManager, Context context) {
            super(fragmentManager);
            this.context = context;
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
                    return context.getString(R.string.tab_title_budget);
                }
                case 1: {
                    return context.getString(R.string.tab_title_expense);
                }
            }

            return null;
        }
    }
}
