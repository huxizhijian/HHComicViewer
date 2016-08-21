package org.huxizhijian.hhcomicviewer2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;

import org.huxizhijian.hhcomicviewer2.utils.Constants;

public class MainActivity extends Activity {


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

    public void open(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }
}
