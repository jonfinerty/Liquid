package com.jonathanfinerty.liquidity.presentation.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.loaders.ExpenseViewModelLoader;
import com.jonathanfinerty.liquidity.presentation.viewmodel.ExpenseViewModel;

public class ExpenseDetailsFragment extends Fragment
                                    implements LoaderManager.LoaderCallbacks<ExpenseViewModel> {

    private static final String TAG = "Expense Details Fragment";

    public static final String EXPENSE_ID_ARGUMENT = "expense id";

    public interface ExpenseDetailsClickListener {
        public abstract void onEditAmountClicked();
        public abstract void onEditDateClicked();
        public abstract void onDeleteExpenseClicked();
    }

    private long expenseId;
    private ExpenseDetailsClickListener expenseDetailsClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View expenseDetailsFragmentView = inflater.inflate(R.layout.fragment_expense_details, container, false);

        expenseId = getArguments().getLong(EXPENSE_ID_ARGUMENT);

        if (expenseId == 0L) {
            Log.e(TAG, "No expense id passed in argument.");
        }

        Button editAmountButton = (Button) expenseDetailsFragmentView.findViewById(R.id.expense_details_button_edit_expense_amount);
        editAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "edit amount clicked");
                expenseDetailsClickListener.onEditAmountClicked();
            }
        });

        Button editDateButton = (Button) expenseDetailsFragmentView.findViewById(R.id.expense_details_button_edit_expense_date);
        editDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "edit date clicked");
                expenseDetailsClickListener.onEditDateClicked();
            }
        });

        Button deleteExpenseButton = (Button) expenseDetailsFragmentView.findViewById(R.id.expense_details_button_delete_expense);
        deleteExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "delete expense clicked");
                expenseDetailsClickListener.onDeleteExpenseClicked();
            }
        });

        getLoaderManager().initLoader(0, null, this);

        return expenseDetailsFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            expenseDetailsClickListener = (ExpenseDetailsClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ExpenseDetailsClickListener");
        }
    }

    @Override
    public Loader<ExpenseViewModel> onCreateLoader(int id, Bundle args) {
        return new ExpenseViewModelLoader(getActivity(), expenseId);
    }

    @Override
    public void onLoadFinished(Loader<ExpenseViewModel> loader, ExpenseViewModel expenseViewModel) {

        if (expenseViewModel == null) {
            return;
        }

        TextView expenseAmountTextView = (TextView) getView().findViewById(R.id.expense_details_textview_expense_amount);
        expenseAmountTextView.setText(expenseViewModel.getHumanReadableValue());

        TextView expenseDateTextView = (TextView) getView().findViewById(R.id.expense_details_textview_expense_date);
        expenseDateTextView.setText(expenseViewModel.getHumanReadableTime());
    }

    @Override
    public void onLoaderReset(Loader<ExpenseViewModel> loader) {

    }

    public void editExpenseAmount(View view) {
        expenseDetailsClickListener.onEditAmountClicked();
    }

    public void editExpenseDate(View view) {
        expenseDetailsClickListener.onEditDateClicked();
    }

    public void deleteExpense(View view) {
        expenseDetailsClickListener.onDeleteExpenseClicked();
    }
}
