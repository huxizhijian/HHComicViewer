package org.huxizhijian.hhcomicviewer2.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * 默认显示一组下拉列表
 * Created by wei on 2016/10/3.
 */

public class InstantAutoCompleteTextView extends AutoCompleteTextView {

    private int myThreshold;

    public InstantAutoCompleteTextView(Context context) {
        super(context);
    }

    public InstantAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstantAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
//            performFiltering(getText(), 0);
            showDropDown();
        }
    }

    public void setThreshold(int threshold) {
        if (threshold < 0) {
            threshold = 0;
        }
        myThreshold = threshold;
    }

    public int getThreshold() {
        return myThreshold;
    }
}
