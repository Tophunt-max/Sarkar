package com.rozgarone;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.android.gms.ads.MobileAds;
import com.rozgarone.ads.AppOpenManager;
import com.rozgarone.net.Api;
import com.rozgarone.net.AppSettings;
import com.rozgarone.push.Push;

/**
 * Application entry point (the original's {@code AppInit.AppCheck}). Owns the process-wide
 * {@link Api} client, the server-driven ad settings, AdMob and the app-open ad manager.
 */
public class App extends Application {

    private static App instance;
    private Api api;
    private AppSettings settings = new AppSettings();
    private SharedPreferences prefs;
    private AppOpenManager appOpen;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        api = new Api(this, BuildConfig.API_BASE);
        prefs = getSharedPreferences("sarkar", MODE_PRIVATE);
        settings = readSettings();

        MobileAds.initialize(this, status -> { });
        appOpen = new AppOpenManager(this);
        Push.init(this);
    }

    public static App get() {
        return instance;
    }

    public Api api() {
        return api;
    }

    public AppSettings settings() {
        return settings;
    }

    /** Stores the latest {@code appsettings.php} values, like the original's SharedPreferences cache. */
    public void setSettings(AppSettings s) {
        this.settings = s == null ? new AppSettings() : s;
        prefs.edit()
                .putString("admob_banner", settings.admob_banner)
                .putString("admob_interstitial", settings.admob_interstitial)
                .putString("admob_native", settings.admob_native)
                .putString("admob_appopen", settings.admob_appopen)
                .apply();
        appOpen.load();
    }

    private AppSettings readSettings() {
        AppSettings s = new AppSettings();
        s.admob_banner = prefs.getString("admob_banner", "");
        s.admob_interstitial = prefs.getString("admob_interstitial", "");
        s.admob_native = prefs.getString("admob_native", "");
        s.admob_appopen = prefs.getString("admob_appopen", "");
        return s;
    }

    public SharedPreferences prefs() {
        return prefs;
    }
}
