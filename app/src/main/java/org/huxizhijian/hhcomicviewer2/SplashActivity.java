package org.huxizhijian.hhcomicviewer2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * 闪屏界面
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView_splash = (ImageView) findViewById(R.id.imageView_splash);
        Glide.with(this)
                .load(R.drawable.splash_page)
                .centerCrop()
                .into(imageView_splash);
        Handler handler = new Handler();
        overridePendingTransition(R.anim.activity_fade_action, R.anim.activity_hold_action);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 2500);
    }
}
