package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.FireStoreHelper.fetchUsernameFromFire;
import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.setToast;
import static com.app.giftxchange.utils.Utils.showProgressDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.adapter.MessageListAdapter;
import com.app.giftxchange.databinding.ActivityChatBinding;
import com.app.giftxchange.model.MessageEntry;
import com.app.giftxchange.model.MessageModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    String userId, clientId,usernameid;
    String msg, status;
    MessageListAdapter firebaseListAdapter;
    private ArrayList<MessageEntry> messageList;
    private ArrayList<MessageModel> clientList;

    ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            getSupportActionBar().setTitle("Chat");
        }

        getDataFromIntent();
        binding.textinput.setText(msg);

        showProgressDialog(this, getString(R.string.please_wait));

        messageList = new ArrayList<>();
        clientList = new ArrayList<>();
        firebaseListAdapter = new MessageListAdapter(messageList, ChatActivity.this, userId, clientId, status);
        binding.rvMessage.setAdapter(firebaseListAdapter);
        binding.rvMessage.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String path;
        if (status.equals("User")) {
            path = String.valueOf(userId + "|" + clientId);
        } else {
            path = String.valueOf(userId + "|" + clientId);
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Log.d("TAG", "onCreate:............................. " + path);
        DatabaseReference myRef = database.getReference("message").child(path);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    hideProgressDialog(ChatActivity.this);
                    messageList.clear();

                    Map<String, String> messagesMap = (Map<String, String>) dataSnapshot.getValue();

                    for (String key : messagesMap.keySet()) {
                        String message = messagesMap.get(key);
                        Log.d("TAG", "onDataChange: ......................." + key + ":" + message);

                        String[] parts = key.split("\\|");

                        messageList.add(new MessageEntry(parts[0], message, Long.parseLong(parts[1])));
                    }
                    Collections.sort(messageList, new Comparator<MessageEntry>() {
                        @Override
                        public int compare(MessageEntry message1, MessageEntry message2) {
                            return Long.compare(message1.getTimestamp(), message2.getTimestamp());
                        }
                    });
                    firebaseListAdapter.notifyDataSetChanged();
                    scrollToBottom();
                } else {
                    hideProgressDialog(ChatActivity.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog(ChatActivity.this);
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef.addValueEventListener(postListener);

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.textinput.getText().toString().equals("") || binding.textinput.getText().toString().length() == 0) {
                    setToast(ChatActivity.this, "Please Write Message!");
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("message");
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts;
                    String path;

                    if (status.equals("User")) {
                        Log.d("TAG", "onClick: .........................." + status);
                        ts = userId + "|" + tsLong.toString();
                        path = String.valueOf(userId + "|" + clientId);
                    } else if (status.equals("Client")) {
                        Log.d("TAG", "onClick: .........................." + status);
                        Log.d("TAG", "onClick: .........................." + clientId);

                        ts = clientId + "|" + tsLong.toString();
                        Log.d("TAG", "onClick: .........................." + ts);
                        path = String.valueOf(userId + "|" + clientId);
                    } else {
                        Log.d("TAG", "onClick: .........................." + status);
                        ts = userId + "|" + tsLong.toString();
                        path = String.valueOf(userId + "|" + clientId);
                    }

                    myRef.child(path).child(ts).setValue(binding.textinput.getText().toString());
                    binding.textinput.setText("");
                }
            }
        });
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent != null) {
            status = intent.getStringExtra("status");
            userId = intent.getStringExtra("userId");
            clientId = intent.getStringExtra("clientId");
            usernameid = intent.getStringExtra("usernameid");
            fetchUsernameFromFire(usernameid, binding.otherUsername, this);
            Log.d("TAG", "getDataFromIntent:............................. " + status + "..........." + userId + "............" + clientId);

        }
    }

    private void scrollToBottom() {
        binding.rvMessage.scrollToPosition(firebaseListAdapter.getItemCount() - 1);
    }
}