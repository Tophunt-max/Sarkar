package com.rozgarone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rozgarone.R;
import com.rozgarone.util.Util;

/** Full post detail: title, publish/expiry dates and rendered HTML body. */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_CONTENT = "content";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_EXPIRY = "expiry";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        findViewById(R.id.backbutton).setOnClickListener(v -> finish());

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String content = getIntent().getStringExtra(EXTRA_CONTENT);
        String date = getIntent().getStringExtra(EXTRA_DATE);
        String expiry = getIntent().getStringExtra(EXTRA_EXPIRY);

        ((TextView) findViewById(R.id.heading)).setText(title == null ? "" : title);
        ((TextView) findViewById(R.id.time)).setText(date == null ? "" : date);
        ((TextView) findViewById(R.id.content)).setText(Util.fromHtml(content));

        TextView lastDate = findViewById(R.id.lastdate);
        if (expiry == null || expiry.trim().isEmpty()) {
            lastDate.setVisibility(android.view.View.GONE);
        } else {
            lastDate.setText(getString(R.string.last_date) + expiry);
        }

        findViewById(R.id.shareBtn).setOnClickListener(v -> {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, title == null ? getString(R.string.app_name) : title);
            send.putExtra(Intent.EXTRA_TEXT,
                    (title == null ? "" : title + "\n\n") + Util.plain(content));
            startActivity(Intent.createChooser(send, getString(R.string.share_app)));
        });
    }
}
