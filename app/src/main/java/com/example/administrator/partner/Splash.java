package com.example.administrator.partner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by FengRui on 2016/2/24.
 */
public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Handler x = new Handler();
        x.postDelayed(new splashHandler(), 2000);

    }

    class splashHandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), Register_Login.class));
            Splash.this.finish();
        }

    }
}
