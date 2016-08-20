package org.huxizhijian.hhcomicviewer2;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.huxizhijian.hhcomicviewer2.utils.Constants;

public class MainActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar(Constants.themeColor);
    }

    private void initActionBar(int newColor) {
        Drawable colorDrawable = new ColorDrawable(newColor);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable});
        android.app.ActionBar actionBar = getActionBar();
        if (actionBar != null) {
//            System.out.println("action bar != null");
            actionBar.setBackgroundDrawable(ld);
        }
    }
}
