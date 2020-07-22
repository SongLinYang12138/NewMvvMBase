package com.bondex.ysl.installlibrary.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.bondex.ysl.installlibrary.R;


/**
 * date: 2018/11/9
 * Author: ysl
 * description:
 */
public class ProgressView extends View {



    /**
     * 进度条最大值，默认为100
     */
    private int maxValue = 100;

    private Paint circlePaint;
    private Paint progresspaint;
    private Paint textPaint;

    private float textSize = 60;
    private float circleWidth = 10;
    float currentValue = 0;
    float alphaAngle = 0;



    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }


    @SuppressLint("ResourceAsColor")
    private void init(Context context, AttributeSet attrs) {

        circlePaint = new Paint();
        progresspaint = new Paint();
        textPaint = new Paint();

        int smallCircleColor = 0;
        int bigCircleColor = 0;
        int textColor = 0;

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);

        int n = ta.getIndexCount();

        for (int i = 0; i < n; ++i) {

            int attr = ta.getIndex(i);
            if (attr == R.styleable.ProgressView_smallCircleColor) {
                smallCircleColor = ta.getColor(attr, Color.GRAY);
            } else if (attr == R.styleable.ProgressView_bigCircleColor) {
                bigCircleColor = ta.getColor(attr, Color.BLUE);
            } else if (attr == R.styleable.ProgressView_textColor) {
                textColor = ta.getColor(attr, Color.BLUE);
            } else if (attr == R.styleable.ProgressView_circleWidth) {
                circleWidth = ta.getFloat(attr, 10);
            } else if (attr == R.styleable.ProgressView_textSize) {
                textSize = ta.getFloat(attr, 60);
            }


        }


        circlePaint.setColor(smallCircleColor);
        circlePaint.setStyle(Paint.Style.FILL);

        progresspaint.setColor(bigCircleColor);
        progresspaint.setAntiAlias(true);
        progresspaint.setStyle(Paint.Style.STROKE);
        progresspaint.setStrokeWidth(circleWidth);

        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);



    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        int center = this.getWidth() / 2;
        int radius = center - (int) (circleWidth / 2);

        drawCircle(canvas, center, radius);
        drawText(canvas, center, (int) currentValue);
    }


    private void drawCircle(Canvas canvas, int center, int radius) {


        RectF rectF = new RectF(center - radius, center - radius, center + radius, center + radius);
        alphaAngle = currentValue * 360.0f / maxValue * 1.0f;

        canvas.drawArc(rectF, -90, alphaAngle, false, progresspaint);


        canvas.drawCircle(center, center, radius - circleWidth, circlePaint);

    }


    private void drawText(Canvas canvas, int center, int currentValue) {

        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();

        // 计算文字的基线,方法见http://blog.csdn.net/harvic880925/article/details/50423762
        int baseline = center + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        canvas.drawText(currentValue + "%", center, baseline, textPaint);
    }


    public void setCurrentValue(float currentValue) {

        this.currentValue = currentValue;
        invalidate();

    }

}
