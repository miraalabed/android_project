package com.example.schoolhub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ReplyViewHolder> {
    private List<ReplyModel> replies;
    private Context context;

    public RepliesAdapter(List<ReplyModel> replies, Context context) {
        this.replies = replies;
        this.context = context;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        ReplyModel reply = replies.get(position);
        holder.textStudent.setText("Student: " + reply.getStudentName());
        holder.textTitle.setText("Assignment: " + reply.getAssignmentTitle());
        holder.textContent.setText("Reply: " + reply.getReplyContent());
        holder.textDate.setText(reply.getCreatedAt());

        if (reply.getReplyLink() != null && !reply.getReplyLink().isEmpty()) {
            holder.textLink.setVisibility(View.VISIBLE);
            holder.textLink.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reply.getReplyLink()));
                context.startActivity(intent);
            });
        } else {
            holder.textLink.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView textStudent, textTitle, textContent, textDate, textLink;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            textStudent = itemView.findViewById(R.id.textStudentName);
            textTitle = itemView.findViewById(R.id.textAssignmentTitle);
            textContent = itemView.findViewById(R.id.textReplyContent);
            textDate = itemView.findViewById(R.id.textReplyDate);
            textLink = itemView.findViewById(R.id.textReplyLink);
        }
    }
}
