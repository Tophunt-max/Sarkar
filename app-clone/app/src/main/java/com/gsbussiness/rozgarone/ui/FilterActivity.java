package com.rozgarone.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.rozgarone.app.R;
import com.rozgarone.app.models.FilterItem;
import com.rozgarone.app.net.Repo;
import com.rozgarone.app.ui.adapter.FilterAdapter;
import com.rozgarone.app.util.Util;

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
        title.setText(group);

        findViewById(R.id.backbutton).setOnClickListener(v -> finish());

        shimmer = findViewById(R.id.shimmer_view_container);
        refresh = findViewById(R.id.swipeRefreshLayout);
        noData = findViewById(R.id.txt_noData);

        adapter = new FilterAdapter(this);
        RecyclerView list = findViewById(R.id.recyc_ipo);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        EditText search = findViewById(R.id.searchview);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                adapter.filter(s == null ? "" : s.toString());
                noData.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        findViewById(R.id.searchBtn).setOnClickListener(v ->
                adapter.filter(search.getText().toString()));

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
        i.putExtra(ListActivity.EXTRA_TITLE, item.category_name);
        i.putExtra(ListActivity.EXTRA_GROUP, group);
        i.putExtra(ListActivity.EXTRA_LABEL_ID, item.id);
        startActivity(i);
    }
}
