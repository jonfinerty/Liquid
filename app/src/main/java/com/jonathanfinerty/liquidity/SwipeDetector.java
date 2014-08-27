package com.jonathanfinerty.liquidity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

public class SwipeDetector implements View.OnTouchListener {
    // Cached ViewConfiguration and system-wide constant values
    private int scaledTouchSlop;
    private int minFlingVelocity;
    private int maxFlingVelocity;
    private long animationTime;

    // Fixed properties
    private ListView listView;
    private DismissCallback dismissCallback;

    // Transient properties
    private float startX;
    private boolean mSwiping;
    private VelocityTracker velocityTracker;
    private int swipedViewPosition;
    private View swipedView;
    private boolean enabled = true;

    public interface DismissCallback {
        void onDismiss(ListView listView, int position);
    }

    public SwipeDetector(ListView listView, DismissCallback callback) {
        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        scaledTouchSlop = vc.getScaledTouchSlop();
        minFlingVelocity = vc.getScaledMinimumFlingVelocity() * 15;
        maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        animationTime = listView.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        this.listView = listView;
        dismissCallback = callback;
    }

    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AbsListView.OnScrollListener makeScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }
        };
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int listViewWidth = listView.getWidth();

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (!enabled) {
                    return false;
                }

                swipedView = getListViewChildSwiped(motionEvent);

                if (swipedView != null) {
                    startX = motionEvent.getRawX();
                    swipedViewPosition = listView.getPositionForView(swipedView);

                    velocityTracker = VelocityTracker.obtain();
                    velocityTracker.addMovement(motionEvent);

                    mSwiping = true;
                }
                return false;
            }

            case MotionEvent.ACTION_CANCEL: {
                cancelSwiping();
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (!mSwiping) {
                    break;
                }

                float deltaX = motionEvent.getRawX() - startX;

                // only allow swipes to the left.
                if (deltaX > 0 ){
                    cancelSwiping();
                    break;
                }

                velocityTracker.addMovement(motionEvent);
                velocityTracker.computeCurrentVelocity(1000);

                float velocityX = velocityTracker.getXVelocity();
                float absVelocityY = Math.abs(velocityTracker.getYVelocity());

                boolean dismiss = false;

                if (Math.abs(deltaX) > listViewWidth / 2) {
                    dismiss = true;
                } else if (minFlingVelocity <= -velocityX && -velocityX <= maxFlingVelocity
                        && absVelocityY < Math.abs(velocityX)
                        && Math.abs(deltaX) > scaledTouchSlop) {
                    dismiss = true;
                }

                if (dismiss) {
                    // dismiss
                    final View dismissedView = swipedView;
                    final int downPosition = swipedViewPosition;

                    swipedView.animate()
                            .translationX(-listViewWidth)
                            .alpha(0)
                            .setDuration(animationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    performDismiss(dismissedView, downPosition);
                                }
                            });

                    mSwiping = false;

                } else {
                    cancelSwiping();
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (!mSwiping) {
                    break;
                }

                float deltaX = motionEvent.getRawX() - startX;

                // only allow swipes to the left.
                if (deltaX > 0 ){
                    break;
                }

                velocityTracker.addMovement(motionEvent);

                if ( Math.abs(deltaX) > scaledTouchSlop) {

                    listView.requestDisallowInterceptTouchEvent(true);

                    // Cancel ListView's touch (un-highlighting the item)
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex()
                                    << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    listView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (mSwiping) {
                    swipedView.setTranslationX(deltaX );
                    swipedView.setAlpha(Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaX) / listViewWidth)));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private void cancelSwiping() {
        if (!mSwiping) {
            return;
        }

        swipedView.animate()
                  .translationX(0)
                  .alpha(1)
                  .setDuration(animationTime)
                  .setListener(null);

        mSwiping = false;
    }

    private View getListViewChildSwiped(MotionEvent motionEvent) {
        int childCount = listView.getChildCount();
        int[] listViewLocation = new int[2];
        listView.getLocationOnScreen(listViewLocation);
        int x = (int) motionEvent.getRawX() - listViewLocation[0];
        int y = (int) motionEvent.getRawY() - listViewLocation[1];

        for (int i = 0; i < childCount; i++) {

            View child = listView.getChildAt(i);
            Rect hitRect = new Rect();
            child.getHitRect(hitRect);

            if (hitRect.contains(x, y)) {
                return child;
            }
        }

        return null;
    }

    private void performDismiss(final View dismissView, final int dismissPosition) {

        final ViewGroup.LayoutParams layoutParams = dismissView.getLayoutParams();
        final int originalHeight = dismissView.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(animationTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dismissCallback.onDismiss(listView, dismissPosition);

                dismissView.setAlpha(1f);
                dismissView.setTranslationX(0);
                layoutParams.height = originalHeight;
                dismissView.setLayoutParams(layoutParams);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height = (Integer) valueAnimator.getAnimatedValue();
                dismissView.setLayoutParams(layoutParams);
            }
        });

        animator.start();
    }
}