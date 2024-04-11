package com.example.spotify_wrapped.ui.dashboard;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_wrapped.R;

public class SummaryViewHolder extends RecyclerView.ViewHolder {
    private TextView dateTextView;
    private TextView termTextView;
    private PastSummariesAdapter.OnItemClickListener listener;

    public SummaryViewHolder(@NonNull View itemView) {
        super(itemView);
        dateTextView = itemView.findViewById(R.id.dateTextView);
        termTextView = itemView.findViewById(R.id.termTextView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }

    public void bind(PastSummary pastSummary) {
        dateTextView.setText(pastSummary.getDate());
        termTextView.setText(pastSummary.getTimeSpan());
    }

    public void setOnItemClickListener(PastSummariesAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
