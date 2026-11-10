package com.rozgarone;

import android.app.Application;
import android.content.SharedPreferences;

import com.rozgarone.net.Api;
import com.rozgarone.net.AppSettings;

/**
 * Application entry point. Owns the process-wide {@link Api} client and the
 * cached app settings fetched from the backend.
 */
public class App extends Application {

    private static App instance;
    private Api api;
    private AppSettings settings = new AppSettings();
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        api = new Api(this, BuildConfig.API_BASE);
        prefs = getSharedPreferences("sarkar", MODE_PRIVATE);
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

    public void setSettings(AppSettings s) {
        this.settings = s == null ? new AppSettings() : s;
    }

    public SharedPreferences prefs() {
        return prefs;
    }

    /** True until the intro ("Find jobs start here") screen has been dismissed once. */
    public boolean needsIntro() {
        return !prefs.getBoolean("intro_done", false);
    }

    public void markIntroDone() {
        prefs.edit().putBoolean("intro_done", true).apply();
    }
}
