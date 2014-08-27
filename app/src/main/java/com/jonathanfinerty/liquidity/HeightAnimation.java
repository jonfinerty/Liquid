package com.jonathanfinerty.liquidity;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;


public class HeightAnimation extends Animation {

    private final float startHeight;
    private final float heightDelta;
    private View view;
    private int viewWidth;


    public HeightAnimation(View view, int targetHeight) {
        this.view = view;
        this.viewWidth = view.getWidth();
        this.startHeight = view.getHeight();
        this.heightDelta = targetHeight - startHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        float newHeight = startHeight + (heightDelta * interpolatedTime);

        view.setLayoutParams(new LinearLayout.LayoutParams(viewWidth, (int) newHeight));
    }
}
