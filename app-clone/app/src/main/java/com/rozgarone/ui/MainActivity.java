package com.rozgarone.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rozgarone.R;
import com.rozgarone.ads.Ads;
import com.rozgarone.net.Repo;

/**
 * Home dashboard: four feed tiles, four filter entries and the notification bell. As in the
 * original, a native ad fills the middle slot and an interstitial is shown after each tap.
 */
public class MainActivity extends AppCompatActivity {

    private Ads.Interstitial interstitial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        interstitial = new Ads.Interstitial(this);
        Ads.nativeAd(this, findViewById(R.id.mobadslayout));

        findViewById(R.id.findgovjob).setOnClickListener(v ->
                openList(Repo.GOVT_JOB, R.string.title_govtjob));
        findViewById(R.id.currentaffairs).setOnClickListener(v ->
                openList(Repo.CURRENT_AFFAIRS, R.string.title_currentaffairs));
        findViewById(R.id.dailynews).setOnClickListener(v ->
                openList(Repo.DAILY_NEWS, R.string.title_dailynews));
        findViewById(R.id.examresult).setOnClickListener(v ->
                openList(Repo.RESULT_POST, R.string.title_exam_results));

        findViewById(R.id.category).setOnClickListener(v -> openFilters("Category"));
        findViewById(R.id.profession).setOnClickListener(v -> openFilters("Profession"));
        findViewById(R.id.qualification).setOnClickListener(v -> openFilters("Qualification"));
        findViewById(R.id.location).setOnClickListener(v -> openFilters("Location"));

        findViewById(R.id.notification).setOnClickListener(v ->
                go(new Intent(this, NotificationActivity.class)));
    }

    private void openList(String feed, int titleRes) {
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra(ListActivity.EXTRA_FEED, feed);
        i.putExtra(ListActivity.EXTRA_TITLE, getString(titleRes));
        go(i);
    }

    private void openFilters(String group) {
        Intent i = new Intent(this, FilterActivity.class);
        i.putExtra(FilterActivity.EXTRA_GROUP, group);
        go(i);
    }

    private void go(Intent i) {
        startActivity(i);
        interstitial.show(this);
    }
}
