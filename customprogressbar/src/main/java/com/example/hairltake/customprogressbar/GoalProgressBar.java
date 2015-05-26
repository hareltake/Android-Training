package com.example.hairltake.customprogressbar;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Hairltake on 2015/5/26.
 */
public class GoalProgressBar extends View {

    private Paint progressPaint;
    private int goal;
    private int progress;
    private boolean isGoalReached;

    private float goalIndicatorHeight;
    private float goalIndicatorThickness;
    private int goalReachedColor;
    private int goalNotReachedColor;
    private int unfilledSectionColor;
    private int barThickness;
    private IndicatorType indicatorType;
    private ValueAnimator barAnimator;

    public enum IndicatorType {
        Line, Circle, Square
    }

    public GoalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // set width
        int width = MeasureSpec.getSize(widthMeasureSpec);

        // set height
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                // we must be exactly the given size
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                // we can not be bigger than the specified height
                height = (int) Math.min(goalIndicatorHeight, heightSize);
                break;
            default:
                // we can be whatever height want
                height = (int) goalIndicatorHeight;
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int halfHeight = getHeight() / 2;
        int progressEndX = (int) (getWidth() * progress / 100f);

        // draw the part of the bar that's filled
        progressPaint.setStrokeWidth(barThickness);
        int color = (progress >= goal) ? goalReachedColor : goalNotReachedColor;
        progressPaint.setColor(color);
        canvas.drawLine(0, halfHeight, progressEndX, halfHeight, progressPaint);

        // draw the unfilled section
        progressPaint.setColor(unfilledSectionColor);
        canvas.drawLine(progressEndX, halfHeight, getWidth(), halfHeight, progressPaint);

        // draw the goal indicator
        int indicatorPosition = (int) (getWidth() * goal / 100f);
        progressPaint.setColor(goalReachedColor);
        progressPaint.setStrokeWidth(goalIndicatorThickness);
        switch (indicatorType) {
            case Line:
                canvas.drawLine(indicatorPosition,
                        halfHeight - goalIndicatorHeight / 2,
                        indicatorPosition,
                        halfHeight + goalIndicatorHeight / 2,
                        progressPaint);
                break;
            case Circle:
                canvas.drawCircle(indicatorPosition,
                        goalIndicatorHeight / 2,
                        goalIndicatorHeight / 2,
                        progressPaint);
                break;
            case Square:
                canvas.drawRect(indicatorPosition - (goalIndicatorHeight / 2),
                        0,
                        indicatorPosition + (goalIndicatorHeight / 2),
                        goalIndicatorHeight,
                        progressPaint);
                break;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        // save our progress
        bundle.putInt("progress", progress);

        // be sure to save all other instance state that may exist
        bundle.putParcelable("superState", super.onSaveInstanceState());

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            // restore our progress
            setProgress(bundle.getInt("progress"));

            // restore all other state
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    private void init(AttributeSet attrs) {
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.GoalProgressBar, 0, 0);
        try {
            setGoalIndicatorHeight(typedArray.getDimensionPixelSize(R.styleable.GoalProgressBar_goalIndicatorHeight, 10));
            setGoalIndicatorThickness(typedArray.getDimensionPixelSize(R.styleable.GoalProgressBar_goalIndicatorThickness, 5));
            setGoalReachedColor(typedArray.getColor(R.styleable.GoalProgressBar_goalReachedColor, Color.BLUE));
            setGoalNotReachedColor(typedArray.getColor(R.styleable.GoalProgressBar_goalNotReachedColor, Color.BLACK));
            setUnfilledSectionColor(typedArray.getColor(R.styleable.GoalProgressBar_unfilledSectionColor, Color.RED));
            setBarThickness(typedArray.getDimensionPixelOffset(R.styleable.GoalProgressBar_barThickness, 4));

            int index = typedArray.getInt(R.styleable.GoalProgressBar_indicatorType, 0);
            setIndicatorType(IndicatorType.values()[index]);
        } finally {
            typedArray.recycle();
        }
    }


    public void setGoalIndicatorHeight(float goalIndicatorHeight) {
        this.goalIndicatorHeight = goalIndicatorHeight;
        postInvalidate();
    }

    public void setGoalIndicatorThickness(float goalIndicatorThickness) {
        this.goalIndicatorThickness = goalIndicatorThickness;
        postInvalidate();
    }

    public void setGoalReachedColor(int goalReachedColor) {
        this.goalReachedColor = goalReachedColor;
        postInvalidate();
    }

    public void setGoalNotReachedColor(int goalNotReachedColor) {
        this.goalNotReachedColor = goalNotReachedColor;
        postInvalidate();
    }

    public void setUnfilledSectionColor(int unfilledSectionColor) {
        this.unfilledSectionColor = unfilledSectionColor;
        postInvalidate();
    }

    public void setBarThickness(int barThickness) {
        this.barThickness = barThickness;
        postInvalidate();
    }

    public void setIndicatorType(IndicatorType indicatorType) {
        this.indicatorType = indicatorType;
        postInvalidate();
    }

    public void setProgress(final int progress, boolean animate) {
        if(animate) {
            barAnimator = ValueAnimator.ofFloat(0, 1);

            barAnimator.setDuration(700);

            setProgress(0, false);

            barAnimator.setInterpolator(new DecelerateInterpolator());

            barAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float interpolation = Float.valueOf(animation.getAnimatedValue().toString());
                    Log.d("Hairltake", String.valueOf(getProgress()));
                    setProgress((int) (interpolation * progress), false);
                }
            });

            if(!barAnimator.isStarted()) {
                barAnimator.start();
            }
        } else {
            this.progress = progress;
            this.progress = progress;
            postInvalidate();
        }
    }

    public void setProgress(int progress) {
        setProgress(progress, true);
    }

    public int getProgress() {
        return progress;
    }

    public void setGoal(int goal) {
        this.goal = goal;
        postInvalidate();
    }
}


