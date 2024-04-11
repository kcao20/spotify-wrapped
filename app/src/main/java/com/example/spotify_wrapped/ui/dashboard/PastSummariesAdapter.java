package com.example.spotify_wrapped.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_wrapped.R;

import java.util.List;

public class PastSummariesAdapter extends RecyclerView.Adapter<SummaryViewHolder> {
    private List<PastSummary> summaries;
    private OnItemClickListener listener;

    public PastSummariesAdapter(List<PastSummary> summaries) {
        this.summaries = summaries;
    }

    @NonNull @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.past_summaries_list_item, parent, false);
        return new SummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        PastSummary pastSummary = summaries.get(position);
        holder.bind(pastSummary);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return summaries.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
