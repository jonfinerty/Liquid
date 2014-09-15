package com.jonathanfinerty.liquidity.presentation.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.jonathanfinerty.liquidity.R;

public class BudgetTankView extends View {


    private final int backgroundColor = getResources().getColor(R.color.white);
    private final int lineColor = getResources().getColor(R.color.blue_dark);
    private final int tankBackgroundColor = getResources().getColor(R.color.grey_disabled);
    private final int fillColor = getResources().getColor(R.color.green);

    private Paint backgroundPaint;
    private Paint tankBackgroundPaint;
    private Paint tankFillPaint;

    private Paint transparentPaint;

    private Paint tankThinLinePaint;
    private Paint tankThickLinePaint;
    private Paint dateLinePaint;

    private float datePercent;
    private float spentPercent;


    public BudgetTankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setupAttributes(attrs);
    }

    public float getDatePercent() {
        return datePercent;
    }

    public void setDatePercent(float percent) {
        percent = Math.min(percent, 100f);
        percent = Math.max(percent, 0f);
        this.datePercent = percent;
        invalidate();
        requestLayout();
    }

    public float getSpentPercent() {
        return datePercent;
    }

    public void setSpentPercent(float percent) {
        percent = Math.min(percent, 100f);
        percent = Math.max(percent, 0f);
        this.spentPercent = percent;
        invalidate();
        requestLayout();
    }

    private void setupAttributes(AttributeSet attrs) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BudgetTankView, 0, 0);

        try {
            datePercent = a.getFloat(R.styleable.BudgetTankView_datePercent, 0f);
            datePercent = Math.min(datePercent, 100f);
            datePercent = Math.max(datePercent, 0f);

            spentPercent = a.getFloat(R.styleable.BudgetTankView_spentPercent, 0f);
            spentPercent = Math.min(spentPercent, 100f);
            spentPercent = Math.max(spentPercent, 0f);

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTankFill(canvas);
        drawTank(canvas);
        drawDateLine(canvas);
    }

    private void drawTankFill(Canvas canvas) {

        float cornerRadius = 50;

        float viewWidth = getWidth();
        float tankHeight = getHeight();

        float tankLeft = 50;
        float tankRight = viewWidth - 50;

        float tankFillYStart = (spentPercent * tankHeight ) / 100f;

        // draw tank background
        canvas.drawRect(new RectF(tankLeft, 0, tankRight, tankHeight), tankBackgroundPaint);

        // draw green fill
        canvas.drawRect(new RectF(tankLeft, tankFillYStart, tankRight, tankHeight), tankFillPaint);

        // draw white overlap
        Bitmap tankMaskBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tankMaskCanvas = new Canvas(tankMaskBitmap);
        tankMaskCanvas.drawRect(new RectF(0, 0, getWidth(), getHeight()), backgroundPaint);
        // erase rounded rectangle over tank
        tankMaskCanvas.drawRoundRect(new RectF(tankLeft, 0, tankRight, tankHeight), cornerRadius, cornerRadius, transparentPaint);

        // apply mask to original canvas
        canvas.drawBitmap(tankMaskBitmap, new Rect(0, 0, tankMaskBitmap.getWidth(), tankMaskBitmap.getHeight()), new Rect(0, 0, getWidth(), getHeight()), null);
    }

    private void drawTank(Canvas canvas) {
        int viewWidth = getWidth();
        int tankHeight = getHeight();

        int tankLeft = 50;
        int tankRight = viewWidth - 50;
        int tankWidth = tankRight - tankLeft;

        int tankMidX = tankLeft + (tankWidth / 2);
        int tank34X = tankLeft + ((tankWidth / 4) * 3);

        int tankLineYGap = tankHeight / 20;

        for (int i=1; i < 20; i++ ){

            int lineHeight = tankLineYGap * i;

            if (i % 5 == 0) {
                canvas.drawLine(tankMidX, lineHeight, tankRight - (tankWidth / 10), lineHeight, tankThickLinePaint);
            } else {
                canvas.drawLine(tank34X, lineHeight, tankRight - (tankWidth / 10), lineHeight, tankThinLinePaint);
            }
        }
    }

    private void drawDateLine(Canvas canvas) {

        float tankHeight = getHeight();
        float strokeWidth = dateLinePaint.getStrokeWidth();

        int lineXStart = getWidth() - 54;
        int lineXEnd = 0;

        float lineHeight = (tankHeight / 100f) * datePercent;

        lineHeight += (strokeWidth / 2);

        lineHeight = Math.min(lineHeight, tankHeight - (strokeWidth /2));

        canvas.drawLine(lineXStart, lineHeight , lineXEnd, lineHeight, dateLinePaint);
    }

    private void setupPaint() {

        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);

        tankBackgroundPaint = new Paint();
        tankBackgroundPaint.setColor(tankBackgroundColor);

        tankFillPaint = new Paint();
        tankFillPaint.setColor(fillColor);
        tankFillPaint.setAntiAlias(true);

        transparentPaint = new Paint();
        transparentPaint.setAlpha(255);
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        tankThinLinePaint = new Paint();
        tankThinLinePaint.setColor(lineColor);
        tankThinLinePaint.setAntiAlias(true);
        tankThinLinePaint.setStrokeWidth(2);
        tankThinLinePaint.setStyle(Paint.Style.STROKE);

        tankThickLinePaint = new Paint();
        tankThickLinePaint.setColor(lineColor);
        tankThickLinePaint.setAntiAlias(true);
        tankThickLinePaint.setStrokeWidth(4);
        tankThickLinePaint.setStyle(Paint.Style.STROKE);

        dateLinePaint = new Paint();
        dateLinePaint.setColor(lineColor);
        dateLinePaint.setAntiAlias(true);
        dateLinePaint.setStrokeWidth(8);
        dateLinePaint.setStyle(Paint.Style.STROKE);
        dateLinePaint.setStrokeCap(Paint.Cap.SQUARE);
        dateLinePaint.setPathEffect(new DashPathEffect(new float[]{ 15, 30 }, 0));
    }

}
