package com.app.giftxchange.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.ChatActivity;
import com.app.giftxchange.model.UserModel;
import com.app.giftxchange.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameText.setText(context.getString(R.string.ll_userName));
        if (model.getUserId().equals(Utils.currentUserId())) {
            holder.usernameText.setText(Utils.getSharedData(context, context.getString(R.string.ll_userName), null) + " (Me)");
        }

        holder.itemView.setOnClickListener(v -> {
            //navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            //   Utils.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
        }
    }
}
