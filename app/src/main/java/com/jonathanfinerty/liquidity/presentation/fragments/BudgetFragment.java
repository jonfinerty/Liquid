package com.jonathanfinerty.liquidity.presentation.fragments;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.loaders.BudgetTankViewModelLoader;
import com.jonathanfinerty.liquidity.presentation.HeightAnimation;
import com.jonathanfinerty.liquidity.presentation.viewmodel.BudgetTankViewModel;
import com.jonathanfinerty.liquidity.presentation.views.TankView;

public class BudgetFragment extends Fragment
                            implements LoaderManager.LoaderCallbacks<BudgetTankViewModel>{

    private static final String TAG = "Budget Fragment";
    private final int ANIMATION_DURATION = 2000;

    private float datePercent = 0f;
    private float spentPercent = 0f;
    private int tankColour;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        tankColour = getResources().getColor(R.color.green);
    }

    private void animateLabels(float newDatePercent, float newSpentPercent) {
        View rootView = getView();

        if (rootView == null){
            Log.e(TAG, "Error getting view");
            return;
        }

        final TextView todayTextView = (TextView) rootView.findViewById(R.id.textView_today);

        int parentHeight = rootView.getHeight() - 120;

        int maxMarginTop = parentHeight - todayTextView.getHeight();

        int unBoundedTargetMarginTop = (int) (maxMarginTop * newDatePercent);

        final int targetMarginTop = Math.max(unBoundedTargetMarginTop, 20);

        final ViewGroup.MarginLayoutParams layout = (ViewGroup.MarginLayoutParams) todayTextView.getLayoutParams();
        final int startMarginTop = layout.topMargin;

        final int marginDelta = targetMarginTop - startMarginTop;

        Animation dateTextViewAnimation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float newMarginTop = startMarginTop + (marginDelta * interpolatedTime);

                layout.setMargins(layout.leftMargin, (int) newMarginTop, layout.rightMargin, layout.bottomMargin);
                todayTextView.setLayoutParams(layout);
            }
        };

        dateTextViewAnimation.setDuration(ANIMATION_DURATION);

        LinearLayout spentTextLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout_spent_labels);

        int layoutHeight = spentTextLayout.getHeight();

        TextView spentTextView = (TextView) rootView.findViewById(R.id.textView_spent);
        TextView leftTextView = (TextView) rootView.findViewById(R.id.textView_left);

        float spentTargetAlpha;

        if (newSpentPercent > 1f){
            newSpentPercent = 1f - Math.min(newSpentPercent - 1f, 1f);
            spentTargetAlpha = 0f;
        } else {
            spentTargetAlpha = 1f;
        }

        int spentTargetHeight = (int) ((layoutHeight - 100) * newSpentPercent);
        spentTargetHeight = Math.max(60, spentTargetHeight);


        HeightAnimation spentAnimation = new HeightAnimation(spentTextView, spentTargetHeight);
        spentAnimation.setDuration(ANIMATION_DURATION);

        AlphaAnimation spentAlphaAnimation = new AlphaAnimation(spentTextView.getAlpha(), spentTargetAlpha);
        spentAlphaAnimation.setDuration(ANIMATION_DURATION);

        int leftTargetHeight = Math.max(100, layoutHeight - spentTargetHeight);

        HeightAnimation leftAnimation = new HeightAnimation(leftTextView, leftTargetHeight);
        leftAnimation.setDuration(ANIMATION_DURATION);

        AnimationSet textViewsAnimationSet = new AnimationSet(false);
        textViewsAnimationSet.setFillAfter(true);

        textViewsAnimationSet.addAnimation(spentAlphaAnimation);
        textViewsAnimationSet.addAnimation(spentAnimation);

        spentTextView.startAnimation(textViewsAnimationSet);

        todayTextView.startAnimation(dateTextViewAnimation);

        leftTextView.startAnimation(leftAnimation);
    }

    private void animateTank(float newDatePercent, float newSpentPercent) {
        View view = getView();

        if (view == null) {
            Log.e(TAG, "Error getting view");
            return;
        }

        TankView budgetTank = (TankView) view.findViewById(R.id.budget_tank);

        int newTankColour;

        float[] spentValues;

        if (spentPercent <= 1f && newSpentPercent <= 1f) { // under-to-under budget

            newTankColour = getResources().getColor(R.color.green);
            spentValues = new float[]{ (1f - spentPercent), (1f - newSpentPercent) };

        } else if (spentPercent <= 1f && newSpentPercent > 1f) { // under-to-over budget

            newTankColour = getResources().getColor(R.color.red);
            float limitedNewSpentPercent = Math.min(newSpentPercent - 1f, 1f);
            spentValues = new float[]{ (1f - spentPercent), 0f, limitedNewSpentPercent };

        } else if (spentPercent > 1f && newSpentPercent <= 1f) { // over-to-under budget

            newTankColour = getResources().getColor(R.color.green);
            float limitedSpentPercent = Math.min(spentPercent - 1f, 1f);
            spentValues = new float[]{ limitedSpentPercent, 0f, (1f - newSpentPercent) };

        } else { // over-to-over budget

            newTankColour = getResources().getColor(R.color.red);
            float limitedSpentPercent = Math.min(spentPercent - 1f, 1f);
            float limitedNewSpentPercent = Math.min(newSpentPercent - 1f, 1f);
            spentValues = new float[]{ limitedSpentPercent, limitedNewSpentPercent };
        }

        ObjectAnimator dateLineAnimation = ObjectAnimator.ofFloat(budgetTank, "lineHeight", (1f - datePercent), (1f - newDatePercent));
        dateLineAnimation.setDuration(ANIMATION_DURATION);

        ObjectAnimator spentAnimation = ObjectAnimator.ofFloat(budgetTank, "filled", spentValues);
        spentAnimation.setDuration((ANIMATION_DURATION / 2) * spentValues.length);

        ObjectAnimator colorAnimation = ObjectAnimator.ofInt(budgetTank, "fillColor", tankColour, newTankColour);
        colorAnimation.setDuration(ANIMATION_DURATION);
        colorAnimation.setEvaluator(new ArgbEvaluator());

        AnimatorSet animationSetDateFillAndColour = new AnimatorSet();
        animationSetDateFillAndColour.playTogether(dateLineAnimation, spentAnimation, colorAnimation);
        animationSetDateFillAndColour.start();

        datePercent = newDatePercent;
        spentPercent = newSpentPercent;
        tankColour = newTankColour;
    }

    private void updateTank(BudgetTankViewModel budgetTankViewModel) {

        View view = getView();

        if (view == null) {
            Log.e(TAG, "Error getting view");
            return;
        }

        final float newDatePercent = budgetTankViewModel.getDatePercent();

        final float newSpentPercent = budgetTankViewModel.getSpentPercent();

        TextView spentTextView = (TextView) view.findViewById(R.id.textView_spent);
        String spentText = String.format("£%d Spent", Math.round(budgetTankViewModel.getSpent() / 100f));
        spentTextView.setText(spentText);

        TextView leftTextView = (TextView) view.findViewById(R.id.textView_left);
        String leftText;

        if (newSpentPercent <= 1f) {
            leftText = String.format("£%d Left", Math.round((budgetTankViewModel.getAmount() - budgetTankViewModel.getSpent()) / 100f));
        } else {
            leftText = String.format("£%d Over Budget", Math.round((budgetTankViewModel.getSpent() - budgetTankViewModel.getAmount())/ 100f));
        }

        leftTextView.setText(leftText);

        // todo: this is crap
        final ViewTreeObserver viewTreeObserver = getView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                animateLabels(newDatePercent, newSpentPercent);
                animateTank(newDatePercent, newSpentPercent);
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
