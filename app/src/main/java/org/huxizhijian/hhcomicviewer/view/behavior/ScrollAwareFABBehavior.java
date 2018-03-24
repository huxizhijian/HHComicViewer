/*
 * Copyright 2018 huxizhijian
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

package org.huxizhijian.hhcomicviewer.view.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.View;

/**
 * 下拉隐藏behavior
 * Created by wei on 2017/1/20.
 */

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    //我们还可以加一个加速器实现弹射效果
    private FastOutLinearInInterpolator folistener = new FastOutLinearInInterpolator();

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    //正在滑出来
    private boolean isAnimatingOut = false;

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        //不断的调用
        //判断滑动的方向 dyConsumed 某个方向的增量
        if (dyConsumed > 0 && !isAnimatingOut && child.getVisibility() == View.VISIBLE) {
            //fab划出去
            animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            //fab划进来
            animateIn(child);
        }
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    //滑进来
    private void animateIn(FloatingActionButton child) {
        child.setVisibility(View.VISIBLE);
        //属性动画
        ViewCompat.animate(child).translationX(0).setInterpolator(folistener).setListener(null).start();
    }

    //滑出去
    private void animateOut(FloatingActionButton child) {
        //属性动画
        //设置监听判断状态
        ViewCompat.animate(child).translationX(child.getHeight())
                .setInterpolator(folistener).setListener(new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(View view) {
                isAnimatingOut = true;
                super.onAnimationStart(view);
            }

            @Override
            public void onAnimationCancel(View view) {
                isAnimatingOut = false;
                super.onAnimationCancel(view);
            }

            @Override
            public void onAnimationEnd(View view) {
                view.setVisibility(View.GONE);
                isAnimatingOut = false;
                super.onAnimationEnd(view);
            }
        }).start();
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

}
