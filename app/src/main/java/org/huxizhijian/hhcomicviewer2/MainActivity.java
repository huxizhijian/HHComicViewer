package org.huxizhijian.hhcomicviewer2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseUtils.initActionBar(getActionBar(), Constants.THEME_COLOR);
    }

    public void comic(View view) {
        startActivity(new Intent(this, ComicInfoActivity.class));
    }
}
