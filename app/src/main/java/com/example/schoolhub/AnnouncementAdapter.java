package com.example.schoolhub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private List<Announcement> list;

    public AnnouncementAdapter(List<Announcement> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement a = list.get(position);
        holder.title.setText(a.getTitle());
        holder.content.setText("");
        holder.date.setText(a.getDate());

        holder.itemView.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, AnnouncementDetailsActivity.class);
            intent.putExtra("title", a.getTitle());
            intent.putExtra("date", a.getDate());
            intent.putExtra("content", a.getContent());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, date;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.announcementTitle);
            content = view.findViewById(R.id.announcementContent);
            date = view.findViewById(R.id.announcementDate);
        }
    }
}
