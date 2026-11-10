package com.gsbussiness.sarkariexamupdates.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gsbussiness.sarkariexamupdates.App;
import com.gsbussiness.sarkariexamupdates.R;

/** First-run intro: "Find jobs start here", share app, privacy policy. */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        findViewById(R.id.nextpage).setOnClickListener(v -> {
            App.get().markIntroDone();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.sharewithfriend).setOnClickListener(v -> shareApp());

        findViewById(R.id.privacy).setOnClickListener(v ->
                Toast.makeText(this, R.string.privacy_policy, Toast.LENGTH_SHORT).show());
    }

    private void shareApp() {
        String text = getString(R.string.app_name) + "\n"
                + "https://play.google.com/store/apps/details?id=" + getPackageName();
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        send.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(send, getString(R.string.share_app)));
    }
}
