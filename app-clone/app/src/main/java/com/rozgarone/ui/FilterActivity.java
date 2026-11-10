package com.rozgarone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.rozgarone.R;
import com.rozgarone.models.FilterItem;
import com.rozgarone.net.Repo;
import com.rozgarone.ui.adapter.FilterAdapter;
import com.rozgarone.util.Util;

import java.util.List;

/** Lists the values of one filter group, then opens the matching job list. */
public class FilterActivity extends AppCompatActivity implements FilterAdapter.Listener {

    public static final String EXTRA_GROUP = "group";

    private Repo repo;
    private String group;
    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout refresh;
    private TextView noData;
    private FilterAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        group = getIntent().getStringExtra(EXTRA_GROUP);
        if (group == null) group = "Category";

        repo = new Repo(this);

        TextView title = findViewById(R.id.titlebar);
        title.setText(getString(R.string.filter_list_title, group));

        findViewById(R.id.backbutton).setOnClickListener(v -> finish());

        shimmer = findViewById(R.id.shimmer_view_container);
        refresh = findViewById(R.id.swipeRefreshLayout);
        noData = findViewById(R.id.txt_noData);

        adapter = new FilterAdapter(this);
        RecyclerView list = findViewById(R.id.recyc_ipo);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        // SearchView with live filtering, like the original Category screen.
        SearchView search = findViewById(R.id.searchview);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                adapter.filter(text == null ? "" : text);
                noData.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
                return false;
            }
        });

        refresh.setColorSchemeResources(R.color.primary);
        refresh.setOnRefreshListener(this::load);

        load();
    }

    private void load() {
        if (!Util.isOnline(this)) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            shimmer.setVisibility(View.GONE);
            refresh.setRefreshing(false);
            return;
        }
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();
        noData.setVisibility(View.GONE);

        repo.filters(group, new Repo.FiltersCallback() {
            @Override
            public void onSuccess(List<FilterItem> items) {
                if (isFinishing()) return;
                stop();
                adapter.submit(items);
                noData.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                if (isFinishing()) return;
                stop();
                noData.setVisibility(View.VISIBLE);
                Toast.makeText(FilterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stop() {
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        refresh.setRefreshing(false);
    }

    @Override
    public void onPick(FilterItem item) {
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra(ListActivity.EXTRA_FEED, Repo.GOVT_JOB);
        i.putExtra(ListActivity.EXTRA_TITLE, getString(R.string.filter_jobs_title, item.category_name));
        i.putExtra(ListActivity.EXTRA_GROUP, group);
        i.putExtra(ListActivity.EXTRA_LABEL_ID, item.id);
        startActivity(i);
    }
}
