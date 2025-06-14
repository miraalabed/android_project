package com.example.schoolhub;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> list) {
        this.messageList = list;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message msg = messageList.get(position);
        holder.title.setText(msg.title);
        holder.content.setText(msg.content);
        holder.senderTime.setText("From: " + msg.senderType + " at " + msg.sentAt);

        holder.replyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ReplyMessageActivity.class);
            intent.putExtra("message_id", msg.id);
            intent.putExtra("title", msg.title);
            intent.putExtra("sender_id", msg.senderId);


            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, senderTime;
        Button replyBtn;

        public MessageViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_title);
            content = itemView.findViewById(R.id.txt_content);
            senderTime = itemView.findViewById(R.id.txt_sender_time);
            replyBtn = itemView.findViewById(R.id.btn_reply);

        }
    }
}
