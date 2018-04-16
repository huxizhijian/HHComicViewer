/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomicviewer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import org.huxizhijian.hhcomicviewer.R;

/**
 * 自定义ProgressBar
 * Created by wei on 2016/8/11.
 */
public class HorizontalProgressBarWithProgress extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE = 10;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFFC00D1;//color
    private static final int DEFAULT_COLOR_UNREACH = 0xFFD3D6DA;//color
    private static final int DEFAULT_HEIGHT_UNREACH = 2;//dp
    private static final int DEFAULT_COLOR_REACH = 0xFFFC00D1;
    private static final int DEFAULT_HEIGHT_REACH = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp

    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mUnreachColor = DEFAULT_COLOR_UNREACH;
    protected int mUnreachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);
    protected int mReachColor = DEFAULT_COLOR_REACH;
    protected int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);

    protected Paint mPaint = new Paint();

    private int mRealWidth;

    //用户new的时候通常使用
    public HorizontalProgressBarWithProgress(Context context) {
        this(context, null);
    }

    //布局文件中
    public HorizontalProgressBarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyleAttrs(attrs);
        setMax(100);
    }

    /**
     * 获取自定义属性
     * @param attrs
     */
    private void obtainStyleAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs,
                R.styleable.HorizontalProgressBarWithProgress);

        mTextSize = (int) ta
                .getDimension(
                        R.styleable.HorizontalProgressBarWithProgress_progress_text_size,
                        mTextSize);
        mTextColor = ta
                .getColor(
                        R.styleable.HorizontalProgressBarWithProgress_progress_text_color,
                        mTextColor);
        mTextOffset = (int) ta
                .getDimension(
                        R.styleable.HorizontalProgressBarWithProgress_progress_text_offset,
                        mTextOffset);
        mReachColor = ta
                .getColor(R.styleable.HorizontalProgressBarWithProgress_progress_reach_color,
                        mReachColor);
        mReachHeight = (int) ta
                .getDimension(R.styleable.HorizontalProgressBarWithProgress_progress_reach_height,
                        mReachHeight);
        mUnreachColor = ta
                .getColor(R.styleable.HorizontalProgressBarWithProgress_progress_unreach_color,
                        mUnreachColor);
        mUnreachHeight = (int) ta
                .getDimension(R.styleable.HorizontalProgressBarWithProgress_progress_unreach_height,
                        mUnreachHeight);
        //设置paint的值
        mPaint.setTextSize(mTextSize);

        ta.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度不能支持wrap_content，只能给精确值或者match_parent
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int height = measureHeight(heightMeasureSpec);
        //设置值
        setMeasuredDimension(widthSize, height);
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        //用户给了一个精确值
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            //如果用户没有设置值
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            //在三个值里面比较出最大值
            result = getPaddingTop() + getPaddingBottom() + Math.min(Math.max(mReachHeight, mUnreachHeight), Math.abs(textHeight));
        }

        if (mode == MeasureSpec.AT_MOST) {
            //如果用户申请的是父控件的最大值，不能超过最大值
            result = Math.min(result, size);
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        boolean noNeedUnReachBar = false;

        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);

        float radio = getProgress() * 1.0f / getMax();
        float progressX = radio * mRealWidth;
        if (progressX + textWidth > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedUnReachBar = true;
        }

        float endX = progressX - mTextOffset / 2;
        //draw reachBar
        if (endX > 0) {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        //draw text
        mPaint.setColor(mTextColor);
        int y = (int) -(mPaint.descent() + mPaint.ascent() / 2);
        canvas.drawText(text, progressX, y, mPaint);

        //draw unReachBar
        if (!noNeedUnReachBar) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnreachColor);
            mPaint.setStrokeWidth(mUnreachHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();
    }

    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
                getResources().getDisplayMetrics());
    }
}
