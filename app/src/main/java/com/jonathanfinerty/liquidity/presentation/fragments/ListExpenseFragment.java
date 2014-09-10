package com.jonathanfinerty.liquidity.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.domain.Expense; //todo: this should probably not be imported
import com.jonathanfinerty.liquidity.operations.DeleteExpenseOperation;
import com.jonathanfinerty.liquidity.persistence.ExpenseRepository;
import com.jonathanfinerty.liquidity.presentation.ExpenseAdapter;
import com.jonathanfinerty.liquidity.presentation.SwipeDetector;
import com.jonathanfinerty.liquidity.presentation.viewmodel.ExpenseViewModel;

import java.util.ArrayList;

public class ListExpenseFragment extends Fragment {

    private ExpenseAdapter expenseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

                                Intent deleteExpense = new Intent(getActivity(), DeleteExpenseOperation.class);
                                deleteExpense.putExtra(DeleteExpenseOperation.EXPENSE_ID_EXTRA, expenseViewModel.getId());

                                getActivity().startService(deleteExpense);
                            }
                        });

        expenseList.setOnTouchListener(swipeDetector);
        expenseList.setOnScrollListener(swipeDetector.makeScrollListener());

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
    public void onResume() {
        super.onResume();

        LoadExpenses();
    }

    private void LoadExpenses() {

        // todo: this mapping from repository -> expense domain object -> viewmodel needs to be moved/improved somehow
        ExpenseRepository expenseRepository = new ExpenseRepository(this.getActivity());

        ArrayList<Expense> expenses = expenseRepository.getAll();

        ArrayList<ExpenseViewModel> expenseViewModels = new ArrayList<ExpenseViewModel>();

        for (Expense expense : expenses) {
            expenseViewModels.add(new ExpenseViewModel(expense));
        }

        expenseAdapter.clear();

        expenseAdapter.addAll(expenseViewModels);

        expenseAdapter.notifyDataSetChanged();
    }
}
