package com.jonathanfinerty.liquidity.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.presentation.viewmodel.ExpenseViewModel;

import java.util.ArrayList;

public class ExpenseViewModelAdapter extends ArrayAdapter<ExpenseViewModel> {

    public ExpenseViewModelAdapter(Context context, ArrayList<ExpenseViewModel> expenseViewModels) {
        super(context, R.layout.listitem_expense, expenseViewModels);
    }

    @Override
    public void add(ExpenseViewModel expenseToInsert) {

        if (this.getPosition(expenseToInsert) >=  0) {
            return;
        }

        for (int i=0; i < this.getCount(); i++) {
            ExpenseViewModel expenseAtPosition = getItem(i);

            if (expenseToInsert.compareTo(expenseAtPosition) < 0 ) {
                this.insert(expenseToInsert, i);
                return;
            }
        }

        super.add(expenseToInsert);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ExpenseViewModel expense = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_expense, parent, false);
        }

        TextView expenseValueTextView = (TextView) convertView.findViewById(R.id.textView_expense_value);
        expenseValueTextView.setText(expense.getHumanReadableValue());

        TextView expenseTimeTextView = (TextView) convertView.findViewById(R.id.textView_expense_time);
        expenseTimeTextView.setText(expense.getHumanReadableRelativeTime());

        return convertView;
    }
}
