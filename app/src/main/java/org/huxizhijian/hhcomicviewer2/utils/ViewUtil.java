package org.huxizhijian.hhcomicviewer2.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author huxizhijian 2017/4/13
 */
public class ViewUtil {

    public static int dp2px(int dpVal, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

    public static int sp2px(int spVal, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
                context.getResources().getDisplayMetrics());
    }

}
