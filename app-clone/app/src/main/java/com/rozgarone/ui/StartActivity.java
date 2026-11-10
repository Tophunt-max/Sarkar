package com.rozgarone.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rozgarone.R;
import com.rozgarone.ads.Ads;
import com.rozgarone.push.Push;

/**
 * Start screen shown after every splash, like the original {@code StartScreen}: native ad,
 * "Find jobs start here", "Share App", privacy policy link and double-back-to-exit.
 */
public class StartActivity extends AppCompatActivity {

    private boolean backOnce;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Ads.nativeAd(this, findViewById(R.id.mobadslayout));
        askNotificationPermission();

        findViewById(R.id.nextpage).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        findViewById(R.id.sharewithfriend).setOnClickListener(v -> shareApp());

        findViewById(R.id.privacy).setOnClickListener(v -> openPrivacy());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backOnce) {
                    finishAffinity();
                    return;
                }
                backOnce = true;
                Toast.makeText(StartActivity.this, R.string.double_tap_exit, Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> backOnce = false, 2000L);
            }
        });
    }

    private void openPrivacy() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_url))));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.privacy_policy, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp() {
        String text = getString(R.string.app_name) + "\n" + getString(R.string.share_message)
                + "https://play.google.com/store/apps/details?id=" + getPackageName();
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        send.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(send, getString(R.string.share_app)));
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && Push.enabled(this)
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }
}
