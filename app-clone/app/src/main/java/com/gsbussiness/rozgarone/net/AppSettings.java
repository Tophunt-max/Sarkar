package com.rozgarone.app.net;

/** AdMob unit ids and other server-driven settings. */
public class AppSettings {

    public String admob_banner = "";
    public String admob_interstitial = "";
    public String admob_native = "";
    public String admob_appopen = "";

    public boolean hasBanner() {
        return admob_banner != null && !admob_banner.trim().isEmpty();
    }
}
