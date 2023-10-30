package com.app.giftxchange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.app.giftxchange.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseActivity extends AppCompatActivity {
    EditText txtinput;
    AppCompatButton send;
    RecyclerView rvMessage;

    FirebaseListAdapter firebaseListAdapter;

    String userId, clientId;
    String msg, status;
    private ArrayList<MessageEntry> messageList;
    private ArrayList<MessageModel> clientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        getDataFromIntent();

        txtinput = findViewById(R.id.textinput);
        send = findViewById(R.id.send);
        rvMessage = findViewById(R.id.rvMessage);
        txtinput.setText(msg);
        //   rvMessage.setLayoutManager(new LinearLayoutManager(FirebaseActivity.this));


        messageList = new ArrayList<>();
        clientList = new ArrayList<>();
        firebaseListAdapter = new FirebaseListAdapter(messageList, FirebaseActivity.this, userId, clientId, status);
        rvMessage.setAdapter(firebaseListAdapter);
        rvMessage.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String path;
        if (status.equals("User")) {
            path = String.valueOf(userId + "|" + clientId);
        } else {
            path = String.valueOf(userId + "|" + clientId);
        }

        Log.d("TAG", "onCreate:............................. " + path);
        DatabaseReference myRef = database.getReference("message").child(path);


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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
                    txtinput.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef.addValueEventListener(postListener);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                myRef.child(path).child(ts).setValue(txtinput.getText().toString());
            }
        });


    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent != null) {

            msg = intent.getStringExtra("msg");
            status = intent.getStringExtra("status");
            userId = intent.getStringExtra("userId");
            clientId = intent.getStringExtra("clientId");
            Log.d("TAG", "getDataFromIntent:............................. " + status + "..........." + userId + "............" + clientId);

        }
    }

    private void scrollToBottom() {
        rvMessage.scrollToPosition(firebaseListAdapter.getItemCount() - 1);
    }
}