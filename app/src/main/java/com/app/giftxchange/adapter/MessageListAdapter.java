package com.app.giftxchange.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.giftxchange.R;
import com.app.giftxchange.model.MessageEntry;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private ArrayList<MessageEntry>  data;
    String clientId,userId;
    String status;
    //private final Fragment eventFragment;

    Context context;

    public MessageListAdapter(ArrayList<MessageEntry> data, Context context, String ul, String cl, String status) {
        this.data =data;
        this.context = context;
        this.userId=ul;
        this.clientId=cl;
        this.status=status;

    }

    @NonNull
    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.ViewHolder holder, int position) {
        MessageEntry item = data.get(position);
        if (item.getKey() != null && item.getValue() != null) {
            String uid;
            if (status.equals("User")){
                uid= String.valueOf(userId);
            }
            else {
                uid= String.valueOf(clientId);
            }

            Log.d("TAG", "onBindViewHolder:..................... "+uid);
            Log.d("TAG", "onBindViewHolder:..............1....... "+userId);
            Log.d("TAG", "onBindViewHolder:.........2............ "+clientId);
            Log.d("TAG", "onBindViewHolder:.........3............ "+status);
            Log.d("TAG", "onBindViewHolder:.........4............ "+item.getKey());
            if (item.getKey().equals(uid)){
                // Convert the timestamp to a ZonedDateTime
                Instant instant = Instant.ofEpochSecond(item.getTimestamp());
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());

                // Define a format for the output time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");

                // Format and print the time
                String formattedTime = zonedDateTime.format(formatter);
                System.out.println("Formatted Time: " + formattedTime);
                holder.keyTextView.setText(formattedTime);
                holder.valueTextView.setText(item.getValue().toString());
                holder.lluser1.setVisibility(View.VISIBLE);
                holder.lluser2.setVisibility(View.GONE);
            }
            else {
                // Convert the timestamp to a ZonedDateTime
                Instant instant = Instant.ofEpochSecond(item.getTimestamp());
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());

                // Define a format for the output time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");

                // Format and print the time
                String formattedTime = zonedDateTime.format(formatter);
                System.out.println("Formatted Time: " + formattedTime);
                holder.keyTextView1.setText(formattedTime);
                holder.valueTextView2.setText(item.getValue().toString());
                holder.lluser2.setVisibility(View.VISIBLE);
                holder.lluser1.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class
    ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lluser1,lluser2;
        private TextView keyTextView,keyTextView1;
        private TextView valueTextView,valueTextView2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            keyTextView = itemView.findViewById(R.id.keyTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
            lluser1 = itemView.findViewById(R.id.lluser1);
            lluser2 = itemView.findViewById(R.id.lluser2);
            keyTextView1 = itemView.findViewById(R.id.keyTextView1);
            valueTextView2 = itemView.findViewById(R.id.valueTextView1);

        }
    }
}
