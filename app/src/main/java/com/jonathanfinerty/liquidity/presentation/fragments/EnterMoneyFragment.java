package com.jonathanfinerty.liquidity.presentation.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jonathanfinerty.liquidity.R;

public class EnterMoneyFragment extends Fragment {

    public static final String DEFAULT_VALUE = "default value";
    public static final String FRAGMENT_TITLE = "enter money title";

    public interface CurrencyEnteredListener {
        public abstract void onCurrencyEntered(int amount);
    }

    private CurrencyEnteredListener currencyEnteredListener;

    private int total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View moneyFragmentView = inflater.inflate(R.layout.fragment_enter_money, container, false);

        String fragmentTitle = getArguments().getString(FRAGMENT_TITLE);

        total = getArguments().getInt(DEFAULT_VALUE, 0);

        updateTotalDisplay(moneyFragmentView);

        if (fragmentTitle != null) {
            TextView title = (TextView) moneyFragmentView.findViewById(R.id.textView_enter_money_title);
            title.setText(fragmentTitle);
        }

        attachButtonClickListeners(moneyFragmentView);

        return moneyFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            currencyEnteredListener = (CurrencyEnteredListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement CurrencyEnteredListener");
        }
    }

    private void attachButtonClickListeners(View rootView) {

        int[] buttonIds = new int[]{
                R.id.button_input_00,
                R.id.button_input_0,
                R.id.button_input_1,
                R.id.button_input_2,
                R.id.button_input_3,
                R.id.button_input_4,
                R.id.button_input_5,
                R.id.button_input_6,
                R.id.button_input_7,
                R.id.button_input_8,
                R.id.button_input_9
        };

        CalculatorButtonClickListener calculatorButtonClickListener = new CalculatorButtonClickListener();

        for(int buttonId : buttonIds){
            Button numberButton = (Button) rootView.findViewById(buttonId);
            numberButton.setOnClickListener(calculatorButtonClickListener);
        }

        Button submitButton = (Button) rootView.findViewById(R.id.button_enter_currency);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencyEnteredListener.onCurrencyEntered(total);
            }
        });

        Button deleteButton = (Button) rootView.findViewById(R.id.button_delete);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total = total / 10;
                updateTotalDisplay();
            }
        });

    }

    private void updateTotalDisplay() {
        updateTotalDisplay(getView());
    }

    private void updateTotalDisplay(View rootView) {

        TextView totalView = (TextView) rootView.findViewById(R.id.textView_expense_total);
        float decimalTotal = ((float) total) / 100f;
        String formattedTotal = "£" + String.format("%.2f", decimalTotal);
        totalView.setText(formattedTotal);

        Button enterButton = (Button) rootView.findViewById(R.id.button_enter_currency);

        if (total > 0) {
            enterButton.setEnabled(true);
        } else {
            enterButton.setEnabled(false);
        }

    }

    private void appendDigitToTotal(int digit) {

        if (total > (Integer.MAX_VALUE / 100)) {
            return;
        }

        total *= 10;
        total += digit;
    }

    private class CalculatorButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String digitString = (String) view.getTag();

            if (digitString.equals("00")) {
                appendDigitToTotal(0);
                appendDigitToTotal(0);
            } else {
                int digit = Integer.parseInt(digitString);
                appendDigitToTotal(digit);
            }

            updateTotalDisplay();
        }
    }

}
