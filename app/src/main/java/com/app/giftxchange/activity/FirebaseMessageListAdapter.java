package com.app.giftxchange.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.FirebaseActivity;
import com.app.giftxchange.activity.MessageModel;

import java.util.ArrayList;

public class FirebaseMessageListAdapter extends RecyclerView.Adapter<FirebaseMessageListAdapter.ViewHolder> {
    private ArrayList<MessageModel>  data;
    String clientId,userId;
    //private final Fragment eventFragment;

    Context context;

    public FirebaseMessageListAdapter(ArrayList<MessageModel> data, Context context, String ul, String cl) {
        this.data =data;
        this.context = context;
        this.userId=ul;
        this.clientId=cl;

    }

    @NonNull
    @Override
    public FirebaseMessageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FirebaseMessageListAdapter.ViewHolder holder, int position) {
        MessageModel item = data.get(position);
        if (item.getUserId() != null && item.getUserid2() != null) {

            holder.lluser1.setVisibility(View.VISIBLE);
            holder.keyTextView.setText(item.getStatus());
        }
        holder.cvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FirebaseActivity.class);

                String message = "Hello, Second Activity!";
                intent.putExtra("msg", message);
                intent.putExtra("status", item.getStatus());
                intent.putExtra("userId", item.getUserId());
                intent.putExtra("clientId", item.getUserid2());
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class
    ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lluser1,lluser2;
        CardView cvView;
        private TextView keyTextView,keyTextView1;
        private TextView valueTextView,valueTextView2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cvView=itemView.findViewById(R.id.event_items);
            keyTextView = itemView.findViewById(R.id.keyTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
            lluser1 = itemView.findViewById(R.id.lluser1);
            lluser2 = itemView.findViewById(R.id.lluser2);
            keyTextView1 = itemView.findViewById(R.id.keyTextView1);
            valueTextView2 = itemView.findViewById(R.id.valueTextView1);

        }
    }
}