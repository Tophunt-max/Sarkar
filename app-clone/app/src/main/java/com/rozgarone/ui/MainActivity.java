package com.rozgarone.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rozgarone.R;
import com.rozgarone.net.Repo;

/** Home dashboard: four feed tiles, four filter entries and the notification bell. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.findgovjob).setOnClickListener(v ->
                openList(Repo.GOVT_JOB, R.string.title_govtjob, false, null, null));
        findViewById(R.id.currentaffairs).setOnClickListener(v ->
                openList(Repo.CURRENT_AFFAIRS, R.string.title_currentaffairs, false, null, null));
        findViewById(R.id.dailynews).setOnClickListener(v ->
                openList(Repo.DAILY_NEWS, R.string.title_dailynews, false, null, null));
        findViewById(R.id.examresult).setOnClickListener(v ->
                openList(Repo.RESULT_POST, R.string.title_exam_results, false, null, null));

        findViewById(R.id.category).setOnClickListener(v -> openFilters("Category"));
        findViewById(R.id.profession).setOnClickListener(v -> openFilters("Profession"));
        findViewById(R.id.qualification).setOnClickListener(v -> openFilters("Qualification"));
        findViewById(R.id.location).setOnClickListener(v -> openFilters("Location"));

        findViewById(R.id.notification).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class)));
    }

    private void openList(String feed, int titleRes, boolean expired, String group, String labelId) {
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra(ListActivity.EXTRA_FEED, feed);
        i.putExtra(ListActivity.EXTRA_TITLE, getString(titleRes));
        i.putExtra(ListActivity.EXTRA_EXPIRED, expired);
        if (group != null) i.putExtra(ListActivity.EXTRA_GROUP, group);
        if (labelId != null) i.putExtra(ListActivity.EXTRA_LABEL_ID, labelId);
        startActivity(i);
    }

    private void openFilters(String group) {
        Intent i = new Intent(this, FilterActivity.class);
        i.putExtra(FilterActivity.EXTRA_GROUP, group);
        startActivity(i);
    }
}
