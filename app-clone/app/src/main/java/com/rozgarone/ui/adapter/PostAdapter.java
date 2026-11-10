package com.rozgarone.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rozgarone.R;
import com.rozgarone.models.Post;
import com.rozgarone.util.Util;

import java.util.ArrayList;
import java.util.List;

/** Renders feed rows: bold title, publish date and a 3-line body preview. */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    public interface Listener {
        void onPostClick(Post post);
    }

    private final List<Post> items = new ArrayList<>();
    private final Listener listener;

    public PostAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<Post> posts) {
        items.clear();
        if (posts != null) items.addAll(posts);
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
        Post p = items.get(position);
        h.heading.setText(p.jobTitle);
        h.time.setText(p.formattedDate);
        h.description.setText(Util.fromHtml(p.jobContent)); // original: ConstClass.b(html)
        h.itemView.setOnClickListener(v -> listener.onPostClick(p));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView heading;
        final TextView time;
        final TextView description;

        VH(@NonNull View v) {
            super(v);
            heading = v.findViewById(R.id.heading);
            time = v.findViewById(R.id.time);
            description = v.findViewById(R.id.descrption);
        }
    }
}
