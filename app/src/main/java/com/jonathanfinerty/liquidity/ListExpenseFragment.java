package com.jonathanfinerty.liquidity;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.jonathanfinerty.liquidity.ContentProvider.LiquidityContract;

import java.util.ArrayList;

public class ListExpenseFragment extends Fragment {

    private int currentPage = 0;
    private final int PAGE_SIZE = 20;

    private ExpenseAdapter expenseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View listFragmentView = inflater.inflate(R.layout.fragment_list_expenses, container, false);

        final ListView expenseList = (ListView) listFragmentView.findViewById(R.id.listview_expenses);

        expenseAdapter = new ExpenseAdapter(listFragmentView.getContext(), new ArrayList<Expense>());

        expenseList.setAdapter(expenseAdapter);

        SwipeDetector swipeDetector = new SwipeDetector(
                        expenseList,
                        new SwipeDetector.DismissCallback() {
                            public void onDismiss(ListView listView, int position) {

                                Expense item = expenseAdapter.getItem(position);

                                expenseAdapter.remove(item);
                                expenseAdapter.notifyDataSetChanged();

                                getActivity().getContentResolver().delete(item.getContentUri(), null, null);
                            }
                        });

        expenseList.setOnTouchListener(swipeDetector);
        expenseList.setOnScrollListener(swipeDetector.makeScrollListener());

        expenseList.setOnItemClickListener(expenseAdapter);

        View loadMoreButtonView = inflater.inflate(R.layout.fragment_load_more_button, expenseList, false);
        Button button = (Button) loadMoreButtonView.findViewById(R.id.button_load_more);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                LoadExpenses(currentPage);
            }
        });

        expenseList.addFooterView(loadMoreButtonView);



        return listFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        currentPage = 0;
        LoadExpenses(currentPage);
    }

    private void LoadExpenses(int page) {
        Uri expensesUri = LiquidityContract.Expense.CONTENT_URI;

        Cursor expenseCursor = getActivity().getContentResolver().query(
                expensesUri,
                new String[]{LiquidityContract.Expense._ID, LiquidityContract.Expense.COLUMN_NAME_VALUE, LiquidityContract.Expense.COLUMN_NAME_TIME},
                null,
                null,
                LiquidityContract.Expense.SORT_ORDER_DEFAULT + " LIMIT " + PAGE_SIZE + " OFFSET " + (PAGE_SIZE*page));

        expenseCursor.moveToFirst();

        int idColumnIndex = expenseCursor.getColumnIndex(LiquidityContract.Expense._ID);
        int valueColumnIndex = expenseCursor.getColumnIndex(LiquidityContract.Expense.COLUMN_NAME_VALUE);
        int timeColumnIndex = expenseCursor.getColumnIndex(LiquidityContract.Expense.COLUMN_NAME_TIME);

        while (!expenseCursor.isAfterLast()) {

            long id = expenseCursor.getLong(idColumnIndex);
            int value = expenseCursor.getInt(valueColumnIndex);
            long time = expenseCursor.getLong(timeColumnIndex);

            Expense expense = new Expense(id, value, time);

            expenseAdapter.add(expense);

            expenseCursor.moveToNext();
        }

        expenseCursor.close();

        expenseAdapter.notifyDataSetChanged();
    }
}
