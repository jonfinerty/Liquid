package com.jonathanfinerty.liquidity.presentation.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.loaders.BudgetTankViewModelLoader;
import com.jonathanfinerty.liquidity.presentation.HeightAnimation;
import com.jonathanfinerty.liquidity.presentation.viewmodel.BudgetTankViewModel;
import com.jonathanfinerty.liquidity.presentation.views.BudgetTankView;

public class BudgetFragment extends Fragment
                            implements LoaderManager.LoaderCallbacks<BudgetTankViewModel>{

    private static final String TAG = "Budget Fragment";
    private final int ANIMATION_DURATION = 2000;

    private float datePercent = 0f;
    private float spentPercent = 0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_budget, container, false);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(0, null, this);
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

    private void updateTank(BudgetTankViewModel budgetTankViewModel) {

        // todo: a bunch of this text formatting can be pushed up to the VM
        final float newDatePercent = budgetTankViewModel.getDatePercent();

        final float spentPercentWithoutLimit = (((float) budgetTankViewModel.getSpent()) / (float) budgetTankViewModel.getAmount()) * 100f;

        final float newSpentPercent = Math.min(spentPercentWithoutLimit, 100f);


        TextView spentTextView = (TextView) getView().findViewById(R.id.textview_spent);
        TextView leftTextView = (TextView) getView().findViewById(R.id.textview_left);

        String spentText = String.format("£%d Spent", Math.round(budgetTankViewModel.getSpent() / 100f));
        String leftText = String.format("£%d Left", Math.round((budgetTankViewModel.getAmount() - budgetTankViewModel.getSpent()) / 100f));

        spentTextView.setText(spentText);
        leftTextView.setText(leftText);

        // todo: this is crap
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

    @Override
    public Loader<BudgetTankViewModel> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "BudgetTankViewModel Loader Created");
        return new BudgetTankViewModelLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<BudgetTankViewModel> loader, BudgetTankViewModel data) {
        Log.d(TAG, "BudgetTankViewModel Loader Finished");
        updateTank(data);
    }

    @Override
    public void onLoaderReset(Loader<BudgetTankViewModel> loader) {
        Log.d(TAG, "BudgetTankViewModel Loader Reset");
    }
}
