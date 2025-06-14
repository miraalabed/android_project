package com.example.schoolhub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignmentReplyAdapter extends RecyclerView.Adapter<AssignmentReplyAdapter.ViewHolder> {

    private Context context;
    private List<AssignmentReply> replyList;

    public AssignmentReplyAdapter(Context context, List<AssignmentReply> replyList) {
        this.context = context;
        this.replyList = replyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reply_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssignmentReply reply = replyList.get(position);
        holder.tvStudentName.setText(reply.getStudentName());
        holder.tvReplyText.setText(reply.getReplyText());

        String link = reply.getReplyLink();

        if (link != null && !link.isEmpty()) {
            holder.btnOpenLink.setVisibility(View.VISIBLE);
            holder.btnOpenLink.setOnClickListener(v -> {
                try {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    if (link.endsWith(".pdf")) {
                        intent.setDataAndType(uri, "application/pdf");
                    } else if (link.endsWith(".jpg") || link.endsWith(".jpeg") || link.endsWith(".png")) {
                        intent.setDataAndType(uri, "image/*");
                    } else if (link.endsWith(".doc") || link.endsWith(".docx")) {
                        intent.setDataAndType(uri, "application/msword");
                    } else {
                        intent.setDataAndType(uri, "*/*");
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Cannot open the file", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.btnOpenLink.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvReplyText;
        Button btnOpenLink;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvReplyText = itemView.findViewById(R.id.tvReplyText);
            btnOpenLink = itemView.findViewById(R.id.btnOpenLink);
        }
    }
}
