package com.rozgarone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rozgarone.App;
import com.rozgarone.R;
import com.rozgarone.ads.Ads;
import com.rozgarone.net.AppSettings;
import com.rozgarone.net.Repo;
import com.rozgarone.util.Util;

/**
 * Branded splash, same flow as the original {@code SplashScreen}: fetch
 * {@code appsettings.php}, cache the ad unit ids, preload an interstitial, then after
 * {@link #SPLASH_MS} open the start screen and show the interstitial.
 */
public class SplashActivity extends AppCompatActivity {

    /** The original waits 6000 ms after the settings response before moving on. */
    private static final long SPLASH_MS = 6000L;
    /** Used when settings cannot be fetched, so the app is still usable offline. */
    private static final long FALLBACK_MS = 1500L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Ads.Interstitial interstitial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!Util.isOnline(this)) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
            handler.postDelayed(this::route, FALLBACK_MS);
            return;
        }

        new Repo(this).settings(new Repo.SettingsCallback() {
            @Override
            public void onSuccess(AppSettings settings) {
                App.get().setSettings(settings);
                interstitial = new Ads.Interstitial(SplashActivity.this);
                handler.postDelayed(SplashActivity.this::route, SPLASH_MS);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_LONG).show();
                handler.postDelayed(SplashActivity.this::route, FALLBACK_MS);
            }
        });
    }

    private void route() {
        if (isFinishing()) return;
        startActivity(new Intent(this, StartActivity.class));
        if (interstitial != null) interstitial.show(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
