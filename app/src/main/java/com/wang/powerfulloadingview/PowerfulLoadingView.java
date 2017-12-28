package com.wang.powerfulloadingview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wang on 17-11-26.
 */

public class PowerfulLoadingView extends View {

    private static final int ANIMATOR_TIME = 1000;
    private static final int STROKE_WIDTH = 5;
    private static final int DEFAULT_CONTENT_COLOR = Color.WHITE;
    private static final int DEFAULT_LOADING_BAR_COLOR = Color.rgb(65, 105, 225);
    private static final int DEFAULT_BG_COLOR = Color.argb(55, 0, 0, 0);

    private Paint mPaintFill;
    private Paint mPaintStroke;

    private ValueAnimator mCircleRadiusAnimator;
    private ValueAnimator mCircleAngleAnimator;
    private ValueAnimator mTickAnim;
    private ValueAnimator mCrossAnim;
    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private ObjectAnimator mScaleAnimator;

    private Point[] mTickPoint = new Point[3];
    private Point[] mCrossPoint = new Point[4];
    private RectF mRectF = new RectF();

    private int mLoadingBarColor = DEFAULT_LOADING_BAR_COLOR;
    private int mLoadingBgColor = DEFAULT_BG_COLOR;
    private int mTickOrCrossColor = DEFAULT_CONTENT_COLOR;

    private Context mContext;

    public PowerfulLoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public PowerfulLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PowerfulLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mContext = context;

        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setStrokeWidth(STROKE_WIDTH);

        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.PowerfulLoadingView);
            for (int i = 0; i < array.getIndexCount(); i++) {
                int attr = array.getIndex(i);

                switch (attr) {
                    case R.styleable.PowerfulLoadingView_bg_color:
                        mLoadingBgColor = array.getColor(attr, DEFAULT_BG_COLOR);
                        break;

                    case R.styleable.PowerfulLoadingView_loading_bar_color:
                        mLoadingBarColor = array.getColor(attr, DEFAULT_LOADING_BAR_COLOR);
                        break;

                    case R.styleable.PowerfulLoadingView_tick_cross_color:
                        mTickOrCrossColor = array.getColor(attr, DEFAULT_CONTENT_COLOR);
                        break;
                }
            }
            array.recycle();
        }
    }

    public void startLoading() {
        clearAllAnimator();

        //初始化加载条动画，并循环播放
        mCircleAngleAnimator = ValueAnimator.ofFloat(0, 360);
        mCircleAngleAnimator.setDuration(ANIMATOR_TIME);
        mCircleAngleAnimator.setRepeatMode(ValueAnimator.RESTART);
        mCircleAngleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mCircleAngleAnimator.start();
    }

    public void loadSucceed(@Nullable Animator.AnimatorListener listener) {
        clearAllAnimator();

        //初始化向圆心缩小的圆的动画
        mCircleRadiusAnimator = ValueAnimator.ofFloat(0, getWidth() / 2f);
        mCircleRadiusAnimator.setDuration(ANIMATOR_TIME / 2);

        //初始化打钩的动画，并注册监听
        mTickAnim = ValueAnimator.ofInt(0, 255);
        mTickAnim.setDuration(ANIMATOR_TIME / 2);
        if (listener != null) {
            mTickAnim.addListener(listener);
        }

        //放大再回弹的动画
        mScaleAnimator = getScaleAnimator();

        mAnimatorSet.play(mTickAnim).after(mCircleRadiusAnimator).with(mScaleAnimator);
        mAnimatorSet.start();
    }

    public void loadFailed(@Nullable Animator.AnimatorListener listener) {
        clearAllAnimator();

        //初始化向圆心缩小的圆的动画
        mCircleRadiusAnimator = ValueAnimator.ofFloat(0, getWidth() / 2f);
        mCircleRadiusAnimator.setDuration(ANIMATOR_TIME / 2);

        //初始化打叉的动画，并注册监听
        mCrossAnim = ValueAnimator.ofInt(0, 255);
        mCrossAnim.setDuration(ANIMATOR_TIME / 2);
        if (listener != null) {
            mCrossAnim.addListener(listener);
        }

        //放大再回弹的动画
        mScaleAnimator = getScaleAnimator();

        mAnimatorSet.play(mCrossAnim).after(mCircleRadiusAnimator).with(mScaleAnimator);
        mAnimatorSet.start();
    }

    //获取放大再回弹的动画
    private ObjectAnimator getScaleAnimator() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(SCALE_X, 1f, 1.2f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 1f, 1.2f, 1f);

        return ObjectAnimator
                .ofPropertyValuesHolder(this, scaleX, scaleY)
                .setDuration(ANIMATOR_TIME / 2);
    }

    //清空动画
    public void clearAllAnimator() {
        if (mCircleAngleAnimator != null && mCircleAngleAnimator.isRunning()) {
            mCircleAngleAnimator.cancel();
        }

        if (mCircleRadiusAnimator != null && mCircleRadiusAnimator.isRunning()) {
            mCircleRadiusAnimator.cancel();
        }

        if (mTickAnim != null && mTickAnim.isRunning()) {
            mTickAnim.cancel();
        }

        if (mCrossAnim != null && mCrossAnim.isRunning()) {
            mCrossAnim.cancel();
        }

        if (mScaleAnimator != null && mScaleAnimator.isRunning()) {
            mScaleAnimator.cancel();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        measureTickPosition(width / 2, height / 2);
        measureCrossPosition(width / 2, height / 2);
    }

    //测量钩的大小和位置
    private void measureTickPosition(int centerX, int centerY) {
        Point position = new Point();
        position.x = centerX / 2;
        position.y = centerY;
        mTickPoint[0] = position;

        position = new Point();
        position.x = centerX / 10 * 9;
        position.y = centerY + centerY / 3;
        mTickPoint[1] = position;

        position = new Point();
        position.x = centerX + centerX / 2;
        position.y = centerY / 3 * 2;
        mTickPoint[2] = position;
    }

    //测量叉的大小和位置
    private void measureCrossPosition(int centerX, int centerY) {
        Point position = new Point();
        position.x = centerX / 3 * 2;
        position.y = centerY / 3 * 2;
        mCrossPoint[0] = position;

        position = new Point();
        position.x = centerX / 3 * 2;
        position.y = centerY + centerY / 3;
        mCrossPoint[1] = position;

        position = new Point();
        position.x = centerX + centerX / 3;
        position.y = centerY + centerY / 3;
        mCrossPoint[2] = position;

        position = new Point();
        position.x = centerX + centerX / 3;
        position.y = centerY / 3 * 2;
        mCrossPoint[3] = position;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制转动的加载条
        if (mCircleAngleAnimator != null && mCircleAngleAnimator.isRunning()) {
            drawBackground(canvas, mLoadingBgColor);
            mPaintStroke.setColor(mLoadingBarColor);
            mRectF.set(STROKE_WIDTH * 2, STROKE_WIDTH * 2,
                    getWidth() - STROKE_WIDTH * 2, getHeight() - STROKE_WIDTH * 2);
            canvas.drawArc(mRectF, (float) mCircleAngleAnimator.getAnimatedValue(), 270, false, mPaintStroke);
            invalidate();
        }

        //绘制向圆心缩小的圆
        if (mCircleRadiusAnimator != null && mCircleRadiusAnimator.isRunning()) {
            drawBackground(canvas, mLoadingBarColor);
            mPaintFill.setColor(mTickOrCrossColor);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f,
                    getWidth() / 2f - (float) mCircleRadiusAnimator.getAnimatedValue(), mPaintFill);
            invalidate();
        }

        //绘制钩
        if (mTickAnim != null && mTickAnim.isRunning()) {
            drawBackground(canvas, mLoadingBarColor);
            mPaintStroke.setAlpha((int) mTickAnim.getAnimatedValue());
            mPaintStroke.setColor(mTickOrCrossColor);
            mPaintStroke.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawLine(mTickPoint[0].x, mTickPoint[0].y, mTickPoint[1].x, mTickPoint[1].y, mPaintStroke);
            canvas.drawLine(mTickPoint[1].x, mTickPoint[1].y, mTickPoint[2].x, mTickPoint[2].y, mPaintStroke);
            invalidate();
        }

        //绘制叉
        if (mCrossAnim != null && mCrossAnim.isRunning()) {
            drawBackground(canvas, mLoadingBarColor);
            mPaintStroke.setAlpha((int) mCrossAnim.getAnimatedValue());
            mPaintStroke.setColor(mTickOrCrossColor);
            canvas.drawLine(mCrossPoint[0].x, mCrossPoint[0].y, mCrossPoint[2].x, mCrossPoint[2].y, mPaintStroke);
            canvas.drawLine(mCrossPoint[1].x, mCrossPoint[1].y, mCrossPoint[3].x, mCrossPoint[3].y, mPaintStroke);
            invalidate();
        }
    }

    //绘制背景
    private void drawBackground(Canvas canvas, int color) {
        mPaintFill.setColor(color);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, mPaintFill);
    }
}
