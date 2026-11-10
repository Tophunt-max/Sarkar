package com.rozgarone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.rozgarone.R;
import com.rozgarone.models.Post;
import com.rozgarone.net.Repo;
import com.rozgarone.ui.adapter.PostAdapter;
import com.rozgarone.util.Util;

import java.util.List;

/**
 * Generic post list. Serves the four feeds, the expired archive and
 * "jobs by filter" lists, exactly like the original screens did.
 */
public class ListActivity extends AppCompatActivity implements PostAdapter.Listener {

    public static final String EXTRA_FEED = "feed";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_EXPIRED = "expired";
    public static final String EXTRA_GROUP = "group";
    public static final String EXTRA_LABEL_ID = "label_id";

    private Repo repo;
    private PostAdapter adapter;
    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout refresh;
    private TextView noData;
    private TextView expiredToggle;
    private ImageView back;

    private String feed;
    private String group;
    private String labelId;
    private boolean showingExpired;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        feed = getIntent().getStringExtra(EXTRA_FEED);
        if (feed == null) feed = Repo.GOVT_JOB;
        group = getIntent().getStringExtra(EXTRA_GROUP);
        labelId = getIntent().getStringExtra(EXTRA_LABEL_ID);
        showingExpired = getIntent().getBooleanExtra(EXTRA_EXPIRED, false);

        repo = new Repo(this);

        TextView title = findViewById(R.id.titlebar);
        String t = getIntent().getStringExtra(EXTRA_TITLE);
        title.setText(t == null ? getString(R.string.title_govtjob) : t);

        back = findViewById(R.id.backbutton);
        back.setOnClickListener(v -> finish());

        expiredToggle = findViewById(R.id.viewexpired);
        // The expired archive only exists for the govt job feed.
        boolean isGovtJob = Repo.GOVT_JOB.equals(feed);
        expiredToggle.setVisibility(isGovtJob && group == null ? View.VISIBLE : View.GONE);
        expiredToggle.setOnClickListener(v -> {
            showingExpired = !showingExpired;
            expiredToggle.setText(showingExpired ? R.string.view_active : R.string.view_expired);
            load();
        });
        if (isGovtJob && showingExpired) expiredToggle.setText(R.string.view_active);

        shimmer = findViewById(R.id.shimmer_view_container);
        refresh = findViewById(R.id.swipeRefreshLayout);
        noData = findViewById(R.id.txt_noData);

        adapter = new PostAdapter(this);
        RecyclerView list = findViewById(R.id.recyc_ipo);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        refresh.setColorSchemeResources(R.color.primary);
        refresh.setOnRefreshListener(this::load);

        load();
    }

    private void load() {
        if (!Util.isOnline(this)) {
            toast(getString(R.string.no_internet));
            showShimmer(false);
            refresh.setRefreshing(false);
            showEmpty(true);
            return;
        }
        showShimmer(true);
        showEmpty(false);

        Repo.PostsCallback cb = new Repo.PostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (isFinishing()) return;
                showShimmer(false);
                refresh.setRefreshing(false);
                adapter.submit(posts);
                showEmpty(posts.isEmpty());
            }

            @Override
            public void onError(String message) {
                if (isFinishing()) return;
                showShimmer(false);
                refresh.setRefreshing(false);
                showEmpty(true);
                toast(message);
            }
        };

        if (group != null && labelId != null) {
            repo.postsByFilter(group, labelId, cb);
        } else if (showingExpired) {
            repo.expired(cb);
        } else {
            repo.feed(feed, cb);
        }
    }

    private void showShimmer(boolean on) {
        shimmer.setVisibility(on ? View.VISIBLE : View.GONE);
        if (on) shimmer.startShimmer();
        else shimmer.stopShimmer();
    }

    private void showEmpty(boolean empty) {
        noData.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostClick(Post post) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailActivity.EXTRA_TITLE, post.jobTitle);
        i.putExtra(DetailActivity.EXTRA_CONTENT, post.jobContent);
        i.putExtra(DetailActivity.EXTRA_DATE, post.formattedDate);
        i.putExtra(DetailActivity.EXTRA_EXPIRY, post.expiredDate);
        startActivity(i);
    }
}
