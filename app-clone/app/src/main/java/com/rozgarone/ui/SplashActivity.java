package com.rozgarone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rozgarone.App;
import com.rozgarone.R;
import com.rozgarone.net.Repo;

/** Branded splash: prefetches ad settings, then routes to intro or home. */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_MS = 1500L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Repo(this).settings(new Repo.SettingsCallback() {
            @Override
            public void onSuccess(com.rozgarone.net.AppSettings settings) {
                App.get().setSettings(settings);
            }

            @Override
            public void onError(String message) {
                // keep defaults; ads stay disabled
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(this::route, SPLASH_MS);
    }

    private void route() {
        if (isFinishing()) return;
        Class<?> next = App.get().needsIntro() ? StartActivity.class : MainActivity.class;
        startActivity(new Intent(this, next));
        finish();
    }
}
