package com.gsbussiness.sarkariexamupdates.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gsbussiness.sarkariexamupdates.R;
import com.gsbussiness.sarkariexamupdates.models.FilterItem;

import java.util.ArrayList;
import java.util.List;

/** Renders one selectable filter value (e.g. "Banking & Insurance"). */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.VH> {

    public interface Listener {
        void onPick(FilterItem item);
    }

    private final List<FilterItem> items = new ArrayList<>();
    private final List<FilterItem> all = new ArrayList<>();
    private final Listener listener;

    public FilterAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<FilterItem> list) {
        items.clear();
        all.clear();
        if (list != null) {
            items.addAll(list);
            all.addAll(list);
        }
        notifyDataSetChanged();
    }

    /** Local, case-insensitive search used by the search box. */
    public void filter(String query) {
        items.clear();
        if (query == null || query.trim().isEmpty()) {
            items.addAll(all);
        } else {
            String q = query.toLowerCase().trim();
            for (FilterItem f : all) {
                if (f.category_name.toLowerCase().contains(q)) items.add(f);
            }
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.option_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        FilterItem f = items.get(position);
        h.heading.setText(f.category_name);
        h.itemView.setOnClickListener(v -> listener.onPick(f));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView heading;

        VH(@NonNull View v) {
            super(v);
            heading = v.findViewById(R.id.heading);
        }
    }
}
