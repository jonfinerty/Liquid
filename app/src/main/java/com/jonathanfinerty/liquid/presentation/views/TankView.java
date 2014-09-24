package com.jonathanfinerty.liquid.presentation.views;

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

import com.jonathanfinerty.liquid.R;

public class TankView extends View {

    private static final int TANK_SIDE_MARGIN = 50;
    private static final int NUMBER_OF_TANK_LINES = 20;
    private static final int LARGE_LINE_PERIOD = 5;
    private static final int TANK_LINE_RIGHT_MARGIN = 30;

    private final int backgroundColor = getResources().getColor(R.color.white);
    private final int lineColor = getResources().getColor(R.color.blue_dark);
    private final int tankBackgroundColor = getResources().getColor(R.color.grey);


    private Paint backgroundPaint;
    private Paint tankBackgroundPaint;
    private Paint tankFillPaint;

    private Paint transparentPaint;

    private Paint tankThinLinePaint;
    private Paint tankThickLinePaint;
    private Paint dateLinePaint;

    private float lineHeightPercent;
    private float filledPercent;
    private int fillColor;

    public TankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setupAttributes(attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLineHeight(float percent) {
        percent = Math.min(percent, 1f);
        percent = Math.max(percent, 0f);
        this.lineHeightPercent = percent;
        invalidate();
        requestLayout();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setFilled(float percent) {
        percent = Math.min(percent, 1f);
        percent = Math.max(percent, 0f);
        this.filledPercent = percent;
        invalidate();
        requestLayout();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setFillColor(int color) {
        this.fillColor = color;
        invalidate();
        requestLayout();
    }

    private void setupAttributes(AttributeSet attrs) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TankView, 0, 0);

        try {
            lineHeightPercent = a.getFraction(R.styleable.TankView_lineHeight, 1, 1, 0.5f);
            filledPercent = a.getFraction(R.styleable.TankView_filled, 1, 1, 0.5f);
            fillColor = a.getColor(R.styleable.TankView_fillColor, getResources().getColor(R.color.green));
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTankFill(canvas);
        drawTankLines(canvas);
        drawDottedLine(canvas);
    }

    private void drawTankFill(Canvas canvas) {

        float cornerRadius = 50;

        int viewWidth = getWidth();
        int tankHeight = getHeight();

        int tankLeft = TANK_SIDE_MARGIN;
        int tankRight = viewWidth - TANK_SIDE_MARGIN;

        int tankFillYStart = (int)(tankHeight - (filledPercent * tankHeight));

        Rect entireViewRectangle = new Rect(0, 0, viewWidth, tankHeight);
        RectF tankRectangle = new RectF(tankLeft, 0, tankRight, tankHeight);
        Rect tankFilledRectangle = new Rect(tankLeft, tankFillYStart, tankRight, tankHeight);

        // draw tank background
        canvas.drawRect(tankRectangle, tankBackgroundPaint);

        // draw fill
        tankFillPaint.setColor(fillColor);
        canvas.drawRect(tankFilledRectangle, tankFillPaint);

        // draw white overlap
        Bitmap tankMaskBitmap = Bitmap.createBitmap(viewWidth, tankHeight, Bitmap.Config.ARGB_8888);
        Canvas tankMaskCanvas = new Canvas(tankMaskBitmap);
        tankMaskCanvas.drawRect(entireViewRectangle, backgroundPaint);

        // erase rounded rectangle over tank
        tankMaskCanvas.drawRoundRect(tankRectangle, cornerRadius, cornerRadius, transparentPaint);

        // apply mask to original canvas
        canvas.drawBitmap(tankMaskBitmap, entireViewRectangle, entireViewRectangle, null);
    }

    private void drawTankLines(Canvas canvas) {
        int viewWidth = getWidth();
        int tankHeight = getHeight();

        int tankLeft = TANK_SIDE_MARGIN;

        int tankWidth = viewWidth - (2 * TANK_SIDE_MARGIN);
        
        int tankMidX = viewWidth / 2;
        int tank34X = tankLeft + ((tankWidth / 4) * 3);

        int tankEndX = viewWidth - TANK_SIDE_MARGIN - TANK_LINE_RIGHT_MARGIN;

        for (int i=1; i < NUMBER_OF_TANK_LINES; i++ ){

            int lineHeight = (tankHeight * i) / NUMBER_OF_TANK_LINES;

            if (i % LARGE_LINE_PERIOD == 0) {
                canvas.drawLine(tankMidX, lineHeight, tankEndX, lineHeight, tankThickLinePaint);
            } else {
                canvas.drawLine(tank34X, lineHeight, tankEndX, lineHeight, tankThinLinePaint);
            }
        }
    }

    private void drawDottedLine(Canvas canvas) {

        float tankHeight = getHeight();
        float strokeWidth = dateLinePaint.getStrokeWidth();

        int lineXStart = getWidth() - TANK_SIDE_MARGIN - (int)(dateLinePaint.getStrokeWidth() / 2);
        int lineXEnd = 0;

        float lineHeight = tankHeight - (this.lineHeightPercent * tankHeight);

        lineHeight = Math.min(lineHeight, tankHeight - (strokeWidth /2));

        canvas.drawLine(lineXStart, lineHeight, lineXEnd, lineHeight, dateLinePaint);
    }

    private void setupPaint() {

        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);

        tankBackgroundPaint = new Paint();
        tankBackgroundPaint.setColor(tankBackgroundColor);

        tankFillPaint = new Paint();

        transparentPaint = new Paint();
        transparentPaint.setAlpha(255);
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        tankThinLinePaint = new Paint();
        tankThinLinePaint.setColor(backgroundColor);
        tankThinLinePaint.setAntiAlias(true);
        tankThinLinePaint.setStrokeWidth(2);
        tankThinLinePaint.setStyle(Paint.Style.STROKE);

        tankThickLinePaint = new Paint();
        tankThickLinePaint.setColor(backgroundColor);
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
