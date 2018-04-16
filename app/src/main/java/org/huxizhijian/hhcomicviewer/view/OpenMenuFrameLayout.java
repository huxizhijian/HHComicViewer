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
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import org.huxizhijian.hhcomicviewer.view.listener.OnCenterTapListener;
import org.huxizhijian.hhcomicviewer.view.listener.OnLeftOrRightTapListener;

/**
 * Created by wei on 2017/1/21.
 */

public class OpenMenuFrameLayout extends FrameLayout {

    private GestureDetector mGestureDetector;

    private OnCenterTapListener onCenterTapListener;
    private OnLeftOrRightTapListener onLeftOrRightTapListener;

    public OpenMenuFrameLayout(Context context) {
        this(context, null, 0);
    }

    public OpenMenuFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OpenMenuFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                //单击屏幕中心开启菜单功能
                float x_up, y_up;
                x_up = event.getX();
                y_up = event.getY();
                if (onCenterTapListener.isOpen()) {
                    onCenterTapListener.closeMenu();
                    return true;
                }
                if (event.getPointerCount() == 1) {
                    if (onCenterTapListener != null &&
                            (x_up > (getWidth() / 3)) && (x_up < (getWidth() / 3 * 2))) {
                        if ((y_up > (getHeight() / 3))) {
                            //当短按屏幕中心下方时，开启menu
                            if (!onCenterTapListener.isOpen()) {
                                onCenterTapListener.openMenu();
                                return true;
                            }
                        }
                    } else if (onLeftOrRightTapListener != null && x_up < (getWidth() / 3)) {
                        //当短按屏幕左边时，显示前一页
                        onLeftOrRightTapListener.leftTap();
                    } else if (onLeftOrRightTapListener != null && x_up > (getWidth() / 3 * 2)) {
                        //当短按屏幕右边时，显示后一页
                        onLeftOrRightTapListener.rightTap();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (onCenterTapListener == null && onLeftOrRightTapListener == null)
            return false;
        return mGestureDetector.onTouchEvent(ev);
    }

    public void setOnCenterTapListener(OnCenterTapListener onCenterTapListener) {
        this.onCenterTapListener = onCenterTapListener;
    }

    public void setOnLeftOrRightTapListener(OnLeftOrRightTapListener leftOrRightTapListener) {
        this.onLeftOrRightTapListener = leftOrRightTapListener;
    }

}
