package com.rozgarone.push;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Optional Firebase Cloud Messaging setup. Firebase only initialises when the build
 * included app/google-services.json; otherwise every method here is a no-op.
 */
public final class Push {

    /** FCM topic every install subscribes to (the original used "sarkariexamalerts"). */
    public static final String TOPIC = "rozgarone_alerts";

    private static final String TAG = "Push";

    private Push() {
    }

    public static boolean enabled(@NonNull Context context) {
        return !FirebaseApp.getApps(context).isEmpty();
    }

    public static void init(@NonNull Context context) {
        if (!enabled(context)) {
            Log.i(TAG, "Firebase not configured (no google-services.json); push disabled");
            return;
        }
        subscribe();
    }

    static void subscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                .addOnCompleteListener(t -> Log.i(TAG, "subscribe " + TOPIC + ": " + t.isSuccessful()));
    }
}
