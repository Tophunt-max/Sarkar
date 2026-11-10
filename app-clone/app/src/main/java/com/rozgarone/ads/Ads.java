package com.rozgarone.ads;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.rozgarone.App;
import com.rozgarone.R;

/**
 * AdMob placements, mirroring the original app. Unit ids come from
 * {@code api/appsettings.php} (cached by {@link App}); an empty id disables that placement.
 *
 * <ul>
 *   <li>Banner (adaptive) at the bottom of list and detail screens.</li>
 *   <li>Interstitial on splash exit, after each home tile tap and after opening a post.</li>
 *   <li>Native ad in the {@code mobadslayout} slot on the start and home screens.</li>
 *   <li>App-open ad on returning to the foreground: see {@link AppOpenManager}.</li>
 * </ul>
 */
public final class Ads {

    private Ads() {
    }

    private static AdRequest request() {
        return new AdRequest.Builder().build();
    }

    // ------------------------------------------------------------------ banner

    /** Adds an adaptive banner to {@code container}, or does nothing if no unit id is set. */
    public static void banner(@NonNull Activity activity, @Nullable ViewGroup container) {
        String unit = App.get().settings().admob_banner;
        if (container == null || isBlank(unit)) return;
        AdView adView = new AdView(activity);
        adView.setAdUnitId(unit);
        DisplayMetrics m = activity.getResources().getDisplayMetrics();
        int widthDp = (int) (m.widthPixels / m.density);
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, widthDp));
        container.removeAllViews();
        container.addView(adView);
        adView.loadAd(request());
    }

    // ------------------------------------------------------------ interstitial

    /** One preloaded interstitial per screen; {@link #show} displays it once, then reloads. */
    public static final class Interstitial {
        private final Context appContext;
        private InterstitialAd ad;
        private boolean loading;

        public Interstitial(@NonNull Context context) {
            this.appContext = context.getApplicationContext();
            load();
        }

        public void load() {
            String unit = App.get().settings().admob_interstitial;
            if (loading || ad != null || isBlank(unit)) return;
            loading = true;
            InterstitialAd.load(appContext, unit, request(), new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd loaded) {
                    loading = false;
                    ad = loaded;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError error) {
                    loading = false;
                    ad = null;
                }
            });
        }

        /** Shows the ad if one is ready. Returns true when an ad was shown. */
        public boolean show(@NonNull Activity activity) {
            if (ad == null) return false;
            InterstitialAd current = ad;
            ad = null;
            current.show(activity);
            load();
            return true;
        }
    }

    // ------------------------------------------------------------------ native

    /** Loads a native ad into {@code slot} (a FrameLayout with id mobadslayout). */
    public static void nativeAd(@NonNull Activity activity, @Nullable ViewGroup slot) {
        String unit = App.get().settings().admob_native;
        if (slot == null || isBlank(unit)) {
            hideParentSlot(slot);
            return;
        }
        AdLoader loader = new AdLoader.Builder(activity, unit)
                .forNativeAd(nativeAd -> {
                    if (activity.isDestroyed() || activity.isFinishing()) {
                        nativeAd.destroy();
                        return;
                    }
                    NativeAdView view = (NativeAdView) LayoutInflater.from(activity)
                            .inflate(R.layout.ad_native, slot, false);
                    bind(nativeAd, view);
                    slot.removeAllViews();
                    slot.addView(view);
                })
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        hideParentSlot(slot);
                    }
                })
                .build();
        loader.loadAd(request());
    }

    private static void bind(NativeAd ad, NativeAdView view) {
        view.setMediaView(view.findViewById(R.id.ad_media));
        view.setHeadlineView(view.findViewById(R.id.ad_headline));
        view.setBodyView(view.findViewById(R.id.ad_body));
        view.setCallToActionView(view.findViewById(R.id.ad_call_to_action));
        view.setIconView(view.findViewById(R.id.ad_app_icon));

        ((TextView) view.getHeadlineView()).setText(ad.getHeadline());
        if (ad.getBody() == null) {
            view.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            view.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) view.getBodyView()).setText(ad.getBody());
        }
        if (ad.getCallToAction() == null) {
            view.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            view.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) view.getCallToActionView()).setText(ad.getCallToAction());
        }
        if (ad.getIcon() == null) {
            view.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) view.getIconView()).setImageDrawable(ad.getIcon().getDrawable());
            view.getIconView().setVisibility(View.VISIBLE);
        }
        view.setNativeAd(ad);
    }

    private static void hideParentSlot(@Nullable ViewGroup slot) {
        if (slot == null) return;
        View parent = (View) slot.getParent();
        // The home screen wraps the slot in a grey placeholder box; hide it when there is no ad.
        if (parent != null && parent.getId() == R.id.ads) parent.setVisibility(View.GONE);
        else slot.setVisibility(View.GONE);
    }

    static boolean isBlank(@Nullable String s) {
        return s == null || s.trim().isEmpty();
    }
}
