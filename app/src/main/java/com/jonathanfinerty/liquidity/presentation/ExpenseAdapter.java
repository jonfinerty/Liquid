package com.jonathanfinerty.liquidity.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jonathanfinerty.liquidity.R;

import java.util.ArrayList;

public class ExpenseAdapter extends ArrayAdapter<Expense> implements AdapterView.OnItemClickListener {

    public ExpenseAdapter(Context context, ArrayList<Expense> objects) {
        super(context, R.layout.listitem_expense, objects);
    }

    @Override
    public void add(Expense expenseToInsert) {

        if (this.getPosition(expenseToInsert) >=  0) {
            return;
        }

        for (int i=0; i < this.getCount(); i++) {
            Expense expenseAtPosition = getItem(i);

            if (expenseToInsert.getTime() > expenseAtPosition.getTime()) {
                this.insert(expenseToInsert, i);
                return;
            }
        }

        super.add(expenseToInsert);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Expense expense = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_expense, parent, false);
        }

        TextView expenseValueTextView = (TextView) convertView.findViewById(R.id.textview_expense_value);
        expenseValueTextView.setText(expense.getHumanReadableValue());

        TextView expenseTimeTextView = (TextView) convertView.findViewById(R.id.textview_expense_time);
        expenseTimeTextView.setText(expense.getHumanReadableTime());

        return convertView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Expense expenseClicked = getItem(position);

    }
}
