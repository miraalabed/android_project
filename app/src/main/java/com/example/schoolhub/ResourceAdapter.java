package com.example.schoolhub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {

    private final Context context;
    private final List<Resource> list;

    public ResourceAdapter(Context context, List<Resource> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resource res = list.get(position);

        holder.title.setText(res.getTitle());
        holder.description.setText(res.getDescription());
        holder.date.setText(res.getCreatedAt());

        holder.openButton.setOnClickListener(v -> {
            String fileLink = res.getFileLink();
            if (fileLink != null && !fileLink.trim().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileLink));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No file link available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date;
        Button openButton;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.resource_title);
            description = itemView.findViewById(R.id.resource_description);
            date = itemView.findViewById(R.id.resource_date);
            openButton = itemView.findViewById(R.id.btn_open_link);
        }
    }
}
