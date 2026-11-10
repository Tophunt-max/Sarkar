package com.rozgarone.ads;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.rozgarone.App;
import com.rozgarone.ui.SplashActivity;

/**
 * App-open ad shown whenever the app returns to the foreground, like the original
 * {@code AppInit.AppOpenManager} (ProcessLifecycleOwner ON_START). Skipped on the splash
 * screen, which shows its own interstitial, and over another full-screen ad.
 */
public final class AppOpenManager implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private static final long MAX_AGE_MS = 4 * 3600_000L;
    private static final String ADS_SDK_PACKAGE = "com.google.android.gms.ads";

    private final Application app;
    private AppOpenAd ad;
    private long loadedAt;
    private boolean loading;
    private boolean showing;
    @Nullable
    private Activity current;

    public AppOpenManager(@NonNull Application app) {
        this.app = app;
        app.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void load() {
        String unit = App.get().settings().admob_appopen;
        if (loading || isAvailable() || Ads.isBlank(unit)) return;
        loading = true;
        AppOpenAd.load(app, unit, new AdRequest.Builder().build(), new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd loaded) {
                loading = false;
                ad = loaded;
                loadedAt = System.currentTimeMillis();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError error) {
                loading = false;
            }
        });
    }

    private boolean isAvailable() {
        return ad != null && System.currentTimeMillis() - loadedAt < MAX_AGE_MS;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Activity activity = current;
        if (showing || activity == null || activity instanceof SplashActivity
                || activity.getClass().getName().startsWith(ADS_SDK_PACKAGE)) {
            return;
        }
        if (!isAvailable()) {
            load();
            return;
        }
        AppOpenAd toShow = ad;
        toShow.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                ad = null;
                showing = false;
                load();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                ad = null;
                showing = false;
                load();
            }
        });
        showing = true;
        toShow.show(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!showing) current = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (!showing) current = activity;
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (current == activity) current = null;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle state) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }
}
