package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.FireStoreHelper.fetchUsernameFromFire;
import static com.app.giftxchange.utils.FireStoreHelper.username;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.giftxchange.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FirebaseMessageListAdapter.ViewHolder holder, int position) {
        MessageModel item = data.get(position);
        if (item.getUserId() != null && item.getUserid2() != null) {

            holder.row_listinmsg.setVisibility(View.VISIBLE);
            fetchUsernameFromFire(item.getUserid2(), holder.user_name_text, context);
            // holder.keyTextView.setText(item.getStatus());
        }
        holder.row_listinmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FirebaseActivity.class);

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

        LinearLayout lluser1, lluser2, row_listinmsg;
        private TextView keyTextView, keyTextView1, user_name_text;
        private TextView valueTextView, valueTextView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            keyTextView = itemView.findViewById(R.id.keyTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
            lluser1 = itemView.findViewById(R.id.lluser1);
            lluser2 = itemView.findViewById(R.id.lluser2);
            keyTextView1 = itemView.findViewById(R.id.keyTextView1);
            valueTextView2 = itemView.findViewById(R.id.valueTextView1);
            row_listinmsg = itemView.findViewById(R.id.row_listinmsg);
            user_name_text = itemView.findViewById(R.id.user_name_text);
        }
    }
}