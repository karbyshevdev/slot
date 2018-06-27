package com.karbyshev.slot;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {
    private static int WELCOME_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent welcomeIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(welcomeIntent);

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                finish();
            }
        }, WELCOME_TIMEOUT);
    }
}
