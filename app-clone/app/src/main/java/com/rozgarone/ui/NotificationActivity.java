package com.rozgarone.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.rozgarone.R;
import com.rozgarone.models.Notice;
import com.rozgarone.net.Repo;
import com.rozgarone.util.Util;

import java.util.List;

/** Announcement list behind the bell icon. */
public class NotificationActivity extends AppCompatActivity {

    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout refresh;
    private TextView noData;
    private NoticeAdapter adapter;
    private Repo repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ((TextView) findViewById(R.id.titlebar)).setText(R.string.title_notifications);
        findViewById(R.id.viewexpired).setVisibility(View.GONE);
        findViewById(R.id.backbutton).setOnClickListener(v -> finish());

        shimmer = findViewById(R.id.shimmer_view_container);
        refresh = findViewById(R.id.swipeRefreshLayout);
        noData = findViewById(R.id.txt_noData);

        adapter = new NoticeAdapter();
        RecyclerView list = findViewById(R.id.recyc_ipo);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        repo = new Repo(this);
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

        repo.notices(new Repo.NoticesCallback() {
            @Override
            public void onSuccess(List<Notice> items) {
                if (isFinishing()) return;
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
                refresh.setRefreshing(false);
                adapter.submit(items);
                noData.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                if (isFinishing()) return;
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
                refresh.setRefreshing(false);
                noData.setVisibility(View.VISIBLE);
                Toast.makeText(NotificationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.VH> {

        private final java.util.List<Notice> items = new java.util.ArrayList<>();

        void submit(List<Notice> list) {
            items.clear();
            if (list != null) items.addAll(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.job_list_item, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Notice n = items.get(position);
            h.title.setText(n.maintitle);
            h.meta.setText(n.publishdate);
            h.body.setText(Util.plain(n.secondtitle));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView meta;
            final TextView body;

            VH(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.heading);
                meta = v.findViewById(R.id.time);
                body = v.findViewById(R.id.descrption);
            }
        }
    }
}
