package com.rozgarone.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rozgarone.R;
import com.rozgarone.ads.Ads;
import com.rozgarone.net.Repo;
import com.rozgarone.util.Util;

/**
 * Full post detail, matching the original content screens: title, publish date, HTML body in
 * a WebView (first {@code <h1>} hidden), bottom banner. Govt-job posts also show
 * "Last Date" and a reminder button that opens the clock app's set-alarm screen.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_FEED = "feed";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_CONTENT = "content";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_EXPIRY = "expiry";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        findViewById(R.id.backbutton).setOnClickListener(v -> finish());

        String feed = getIntent().getStringExtra(EXTRA_FEED);
        String title = nz(getIntent().getStringExtra(EXTRA_TITLE));
        String content = nz(getIntent().getStringExtra(EXTRA_CONTENT));
        String date = nz(getIntent().getStringExtra(EXTRA_DATE));
        String expiry = nz(getIntent().getStringExtra(EXTRA_EXPIRY));
        boolean isJob = feed == null || Repo.GOVT_JOB.equals(feed);

        ((TextView) findViewById(R.id.heading)).setText(title);
        ((TextView) findViewById(R.id.time)).setText(date);

        WebView web = findViewById(R.id.content);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Same tweak as the original: the title is already shown above the body.
                view.evaluateJavascript("(function(){var h=document.getElementsByTagName('h1')[0];"
                        + "if(h){h.style.display='none';}})()", null);
            }
        });
        web.loadDataWithBaseURL(null, content, "text/html; charset=utf-8", "UTF-8", null);

        TextView lastDate = findViewById(R.id.lastdate);
        View alarm = findViewById(R.id.alarm);
        if (!isJob || expiry.trim().isEmpty()) {
            lastDate.setVisibility(View.GONE);
        } else {
            lastDate.setText(getString(R.string.last_date) + expiry);
        }
        if (isJob) {
            alarm.setVisibility(View.VISIBLE);
            alarm.setOnClickListener(v -> setReminder(title));
        }

        findViewById(R.id.shareBtn).setOnClickListener(v -> {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, title.isEmpty() ? getString(R.string.app_name) : title);
            send.putExtra(Intent.EXTRA_TEXT, (title.isEmpty() ? "" : title + "\n\n") + Util.plain(content));
            startActivity(Intent.createChooser(send, getString(R.string.share_app)));
        });

        Ads.banner(this, findViewById(R.id.adMobView));
    }

    private void setReminder(String title) {
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, title);
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_clock_app, Toast.LENGTH_SHORT).show();
        }
    }

    private static String nz(@Nullable String s) {
        return s == null ? "" : s;
    }
}
