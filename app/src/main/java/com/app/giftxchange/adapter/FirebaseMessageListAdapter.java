package com.app.giftxchange.adapter;

import static com.app.giftxchange.utils.FireStoreHelper.fetchUsernameFromFire;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.ChatActivity;
import com.app.giftxchange.model.MessageModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseMessageListAdapter extends RecyclerView.Adapter<FirebaseMessageListAdapter.ViewHolder> {
    private ArrayList<MessageModel> data;
    String clientId, userId;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("users");
    Context context;

    public FirebaseMessageListAdapter(ArrayList<MessageModel> data, Context context, String ul, String cl) {
        this.data = data;
        this.context = context;
        this.userId = ul;
        this.clientId = cl;

    }

    @NonNull
    @Override
    public FirebaseMessageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelist_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FirebaseMessageListAdapter.ViewHolder holder, int position) {
        MessageModel item = data.get(position);
        if (item.getUserId() != null && item.getUserid2() != null) {


            holder.row_listinmsg.setVisibility(View.VISIBLE);
            //holder.keyTextView.setText(item.getStatus()); // set status of user
            //fetchUsernameFromFire(item.getUserid2(), holder.user_name_text, context);
            if (item.getUserId().equals(userId)) {
                // If getUserId is equal to the current userId, set the username from getUserId2
                fetchUsernameFromFire(item.getUserid2(), holder.user_name_text, context);
            } else if (item.getUserid2().equals(userId)) {
                // If getUserid2 is equal to the current userId, set the username from getUserId
                fetchUsernameFromFire(item.getUserId(), holder.user_name_text, context);
            }
        }
        holder.row_listinmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                String selectedUserId = (item.getUserId().equals(userId)) ? item.getUserid2() : item.getUserId();

                intent.putExtra("status", item.getStatus());
                intent.putExtra("userId", item.getUserId());
                intent.putExtra("clientId", item.getUserid2());
                intent.putExtra("usernameid", selectedUserId);
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

        LinearLayout row_listinmsg;
        TextView last_message_time_text, user_name_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            row_listinmsg = itemView.findViewById(R.id.row_listinmsg);
            user_name_text = itemView.findViewById(R.id.user_name_text);
            last_message_time_text = itemView.findViewById(R.id.last_message_time_text);
        }
    }
}