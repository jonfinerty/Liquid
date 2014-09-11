package com.jonathanfinerty.liquidity.presentation.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.loaders.ExpensesLoader;
import com.jonathanfinerty.liquidity.presentation.ExpenseAdapter;
import com.jonathanfinerty.liquidity.presentation.SwipeDetector;
import com.jonathanfinerty.liquidity.presentation.viewmodel.ExpenseViewModel;
import com.jonathanfinerty.liquidity.services.DeleteExpenseService;

import java.util.ArrayList;

public class ListExpenseFragment extends Fragment
                                 implements LoaderManager.LoaderCallbacks<ArrayList<ExpenseViewModel>> {

    private static final String TAG = "List Expense Fragment";
    private ExpenseAdapter expenseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View listFragmentView = inflater.inflate(R.layout.fragment_list_expenses, container, false);

        final ListView expenseList = (ListView) listFragmentView.findViewById(R.id.listview_expenses);

        expenseAdapter = new ExpenseAdapter(listFragmentView.getContext(), new ArrayList<ExpenseViewModel>());

        expenseList.setAdapter(expenseAdapter);

        SwipeDetector swipeDetector = new SwipeDetector(
                        expenseList,
                        new SwipeDetector.DismissCallback() {
                            public void onDismiss(ListView listView, int position) {

                                ExpenseViewModel expenseViewModel = expenseAdapter.getItem(position);

                                // todo: Should this do the delete operation first and let the callback
                                // from the success of that update the adapter/view? or remove it instantly
                                // providing instant feedback

                                expenseAdapter.remove(expenseViewModel);
                                expenseAdapter.notifyDataSetChanged();

                                Intent deleteExpense = new Intent(getActivity(), DeleteExpenseService.class);
                                deleteExpense.putExtra(DeleteExpenseService.EXPENSE_ID_EXTRA, expenseViewModel.getId());

                                getActivity().startService(deleteExpense);
                            }
                        });

        expenseList.setOnTouchListener(swipeDetector);
        expenseList.setOnScrollListener(swipeDetector.makeScrollListener());

        getLoaderManager().initLoader(0, null, this);

        // todo: put functionality to load expenses by budget period back in
        /*View loadMoreButtonView = inflater.inflate(R.layout.fragment_load_more_button, expenseList, false);
        Button button = (Button) loadMoreButtonView.findViewById(R.id.button_load_more);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                LoadExpenses(currentPage);
            }
        });

        expenseList.addFooterView(loadMoreButtonView);*/



        return listFragmentView;
    }


    @Override
    public Loader<ArrayList<ExpenseViewModel>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "ExpenseViewModel Loader Created");
        return new ExpensesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ExpenseViewModel>> loader, ArrayList<ExpenseViewModel> data) {
        Log.d(TAG, "ExpenseViewModel Loader Finished");
        expenseAdapter.clear();
        expenseAdapter.addAll(data);
        expenseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ExpenseViewModel>> loader) {
        Log.d(TAG, "ExpenseViewModel Loader Reset");
    }
}
