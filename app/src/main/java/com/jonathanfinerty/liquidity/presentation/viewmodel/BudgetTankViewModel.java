package com.jonathanfinerty.liquidity.presentation.viewmodel;

import com.jonathanfinerty.liquidity.domain.Budget;

import java.util.Calendar;

public class BudgetTankViewModel {

    private final int date;
    private final int totalSpent;
    private final int amount;


    public BudgetTankViewModel(Budget budget, int totalSpent) {
        date = budget.getDate();
        amount = budget.getAmount();
        this.totalSpent = totalSpent;
    }

    public int getAmount() {
        return amount;
    }

    public int getSpent() {
        return totalSpent;
    }

    public float getDatePercent() {

        Calendar calendarToday = Calendar.getInstance();
        int currentDay = calendarToday.get(Calendar.DAY_OF_MONTH);

        int totalDaysGoneInBudget;
        int totalDaysInBudget;

        if (currentDay < date) {
            Calendar calendarForLastMonth = Calendar.getInstance();
            calendarForLastMonth.add(Calendar.MONTH, -1);
            int numDaysLastMonth = calendarForLastMonth.getActualMaximum(Calendar.DAY_OF_MONTH);

            int daysPassedLastMonth = Math.max(numDaysLastMonth - date, 0);

            totalDaysGoneInBudget = daysPassedLastMonth + currentDay;
            totalDaysInBudget = daysPassedLastMonth + date;
        } else {
            Calendar calendarForThisMonth = Calendar.getInstance();
            int numDaysThisMonth = calendarForThisMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
            int daysAfterBudgetDayThisMonth = numDaysThisMonth - date;

            Calendar calendarForNextMonth = Calendar.getInstance();
            calendarForNextMonth.add(Calendar.MONTH, 1);
            int numDaysNextMonth = calendarForNextMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
            int daysInNextMonthBeforeBudgetDay = Math.min(date, numDaysNextMonth);

            totalDaysGoneInBudget = currentDay - date;
            totalDaysInBudget = daysAfterBudgetDayThisMonth + daysInNextMonthBeforeBudgetDay;
        }

        totalDaysGoneInBudget += 1;

        return ((float) totalDaysGoneInBudget / (float) totalDaysInBudget);
    }

    public float getSpentPercent() {
        return (((float) getSpent()) / (float) getAmount());
    }
}
