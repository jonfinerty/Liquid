package com.jonathanfinerty.liquidity.presentation.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.jonathanfinerty.liquidity.R;

public class EnterDateFragment extends Fragment {

    public static final String DEFAULT_VALUE = "default value";

    public interface DateEnteredListener {
        public abstract void onDateEntered(int date);
    }

    private DateEnteredListener dateEnteredListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_enter_date, container, false);

        NumberPicker dayPicker = (NumberPicker) view.findViewById(R.id.numberpicker_budget_date);

        dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);

        int startingValue = getArguments().getInt(DEFAULT_VALUE, 1);
        dayPicker.setValue(startingValue);

        Button submitButton = (Button) view.findViewById(R.id.button_set_budget_date);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NumberPicker dayPicker = (NumberPicker) view.findViewById(R.id.numberpicker_budget_date);

                dateEnteredListener.onDateEntered(dayPicker.getValue());

            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dateEnteredListener = (DateEnteredListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DateEnteredListener");
        }
    }

}
