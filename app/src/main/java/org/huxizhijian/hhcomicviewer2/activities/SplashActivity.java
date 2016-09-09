package org.huxizhijian.hhcomicviewer2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.R;

/**
 * 闪屏界面，初始化数据
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView_splash = (ImageView) findViewById(R.id.imageView_splash);
        Glide.with(this)
                .load(R.drawable.splash_page)
                .dontAnimate()
                .centerCrop()
                .into(imageView_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 3000);
    }
}
