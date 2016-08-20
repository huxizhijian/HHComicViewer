package org.huxizhijian.hhcomicviewer2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

/**
 * 闪屏界面
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView_splash = (ImageView) findViewById(R.id.imageView_splash);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash_page);
        imageView_splash.setImageBitmap(bitmap);
        Handler handler = new Handler();
        overridePendingTransition(R.anim.activity_fade_action, R.anim.activity_hold_action);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 3000);
    }
}
