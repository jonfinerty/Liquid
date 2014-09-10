package com.jonathanfinerty.liquidity.presentation.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.domain.Budget;
import com.jonathanfinerty.liquidity.domain.Expense;
import com.jonathanfinerty.liquidity.loaders.BudgetRepository;
import com.jonathanfinerty.liquidity.loaders.ExpenseRepository;
import com.jonathanfinerty.liquidity.presentation.HeightAnimation;
import com.jonathanfinerty.liquidity.presentation.views.BudgetTankView;

import java.util.ArrayList;
import java.util.Calendar;

public class BudgetFragment extends Fragment {

    private final int ANIMATION_DURATION = 2000;

    private float datePercent = 0f;
    private float spentPercent = 0f;

    private Budget budget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_budget, container, false);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        BudgetRepository budgetRepository = new BudgetRepository(this.getActivity());
        budget = budgetRepository.get();

        final float newDatePercent = getDatePercent();
        int spentAmount = getSpentAmount();

        final float spentPercentWithoutLimit = (((float) spentAmount) / (float) budget.getAmount()) * 100f;

        final float newSpentPercent = Math.min(spentPercentWithoutLimit, 100f);


        TextView spentTextView = (TextView) getView().findViewById(R.id.textview_spent);
        TextView leftTextView = (TextView) getView().findViewById(R.id.textview_left);

        String spentText = String.format("£%d Spent", Math.round(spentAmount / 100f));
        String leftText = String.format("£%d Left", Math.round((budget.getAmount() - spentAmount) / 100f));

        spentTextView.setText(spentText);
        leftTextView.setText(leftText);

        final ViewTreeObserver viewTreeObserver = getView().getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                animateTank(newDatePercent, newSpentPercent);
                animateLabels(newDatePercent, newSpentPercent);
            }
        });

        getView().requestLayout();

    }

    private int getSpentAmount() {

        ExpenseRepository expenseRepository = new ExpenseRepository(this.getActivity());

        ArrayList<Expense> expenses = expenseRepository.getForBudgetPeriod(budget);

        int totalSpent = 0;

        for (Expense expense : expenses) {
            totalSpent += expense.getValue();
        }

        return totalSpent;
    }

    private float getDatePercent() {

        int budgetDay = budget.getDate();

        Calendar calendarToday = Calendar.getInstance();
        int currentDay = calendarToday.get(Calendar.DAY_OF_MONTH);

        int totalDaysGoneInBudget;
        int totalDaysInBudget;

        if (currentDay < budgetDay) {
            Calendar calendarForLastMonth = Calendar.getInstance();
            calendarForLastMonth.add(Calendar.MONTH, -1);
            int numDaysLastMonth = calendarForLastMonth.getActualMaximum(Calendar.DAY_OF_MONTH);

            int daysPassedLastMonth = Math.max(numDaysLastMonth - budgetDay, 0);

            totalDaysGoneInBudget = daysPassedLastMonth + currentDay;
            totalDaysInBudget = daysPassedLastMonth + budgetDay;
        } else {
            Calendar calendarForThisMonth = Calendar.getInstance();
            int numDaysThisMonth = calendarForThisMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
            int daysAfterBudgetDayThisMonth = numDaysThisMonth - budgetDay;

            Calendar calendarForNextMonth = Calendar.getInstance();
            calendarForNextMonth.add(Calendar.MONTH, 1);
            int numDaysNextMonth = calendarForNextMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
            int daysInNextMonthBeforeBudgetDay = Math.min(budgetDay, numDaysNextMonth);

            totalDaysGoneInBudget = currentDay - budgetDay;
            totalDaysInBudget = daysAfterBudgetDayThisMonth + daysInNextMonthBeforeBudgetDay;
        }

        totalDaysGoneInBudget += 1;
        return ((float) totalDaysGoneInBudget / (float) totalDaysInBudget) * 100f;
    }

    private void animateLabels(float newDatePercent, float newSpentPercent) {
        View rootView = getView();

        final TextView todayTextView = (TextView) rootView.findViewById(R.id.textview_today);

        int parentHeight = rootView.getHeight();

        int maxMarginTop = parentHeight - todayTextView.getHeight();

        int unBoundedTargetMarginTop = (int) ((maxMarginTop * newDatePercent) / 100f);

        final int targetMarginTop = Math.max(unBoundedTargetMarginTop, 20);

        final ViewGroup.MarginLayoutParams layout = (ViewGroup.MarginLayoutParams) todayTextView.getLayoutParams();
        final int startMarginTop = layout.topMargin;

        final int marginDelta = targetMarginTop - startMarginTop;

        Animation paddingTopAnimation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float newMarginTop = startMarginTop + (marginDelta * interpolatedTime);

                layout.setMargins(layout.leftMargin, (int) newMarginTop, layout.rightMargin, layout.bottomMargin);
                todayTextView.setLayoutParams(layout);
            }
        };

        paddingTopAnimation.setDuration(ANIMATION_DURATION - 50);

        LinearLayout spentTextLayout = (LinearLayout) rootView.findViewById(R.id.linearlayout_spent_labels);

        int layoutHeight = spentTextLayout.getHeight();

        TextView spentTextView = (TextView) rootView.findViewById(R.id.textview_spent);
        TextView leftTextView = (TextView) rootView.findViewById(R.id.textview_left);

        int spentTargetHeight = (int) ((layoutHeight * newSpentPercent) / 100f);
        int leftTargetHeight = layoutHeight - spentTargetHeight;

        HeightAnimation spentAnimation = new HeightAnimation(spentTextView, spentTargetHeight);
        spentAnimation.setDuration(ANIMATION_DURATION);

        HeightAnimation leftAnimation = new HeightAnimation(leftTextView, leftTargetHeight);
        leftAnimation.setDuration(ANIMATION_DURATION);

        todayTextView.startAnimation(paddingTopAnimation);
        spentTextView.startAnimation(spentAnimation);
        leftTextView.startAnimation(leftAnimation);
    }

    private void animateTank(float newDatePercent, float newSpentPercent) {
        BudgetTankView budgetTank = (BudgetTankView) getView().findViewById(R.id.budget_tank);

        ObjectAnimator dateLineAnimation = ObjectAnimator.ofFloat(budgetTank, "datePercent", datePercent, newDatePercent);
        dateLineAnimation.setDuration(ANIMATION_DURATION);

        ObjectAnimator spentAnimation = ObjectAnimator.ofFloat(budgetTank, "spentPercent", spentPercent, newSpentPercent);
        spentAnimation.setDuration(ANIMATION_DURATION);

        AnimatorSet animationSetDateAndSpent = new AnimatorSet();
        animationSetDateAndSpent.playTogether(dateLineAnimation, spentAnimation);
        animationSetDateAndSpent.start();

        datePercent = newDatePercent;
        spentPercent = newSpentPercent;
    }
}
