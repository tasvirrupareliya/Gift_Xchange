package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.adapter.ChatRecyclerAdapter;
import com.app.giftxchange.databinding.ActivityChatBinding;
import com.app.giftxchange.databinding.ActivitySplashBinding;
import com.app.giftxchange.model.ChatMessageModel;
import com.app.giftxchange.model.ChatroomModel;
import com.app.giftxchange.model.UserModel;
import com.app.giftxchange.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    String chatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String currentuserID = getSharedData(ChatActivity.this, getString(R.string.key_userid), null);

        chatroomId = Utils.getChatroomId(Utils.currentUserId(), getSharedData(ChatActivity.this, getString(R.string.ll_userID), null));
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.otherUsername.setText(getSharedData(ChatActivity.this, getString(R.string.ll_userName), null));

        binding.messageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputmessage = binding.chatMessageInput.getText().toString();

                if (inputmessage.isEmpty()) {
                    setToast(ChatActivity.this, "Please Enter Message");
                } else {
                    sendmsgToUser(inputmessage);
                }
            }
        });

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    void setupChatRecyclerView() {
        Query query = Utils.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        binding.chatRecyclerView.setLayoutManager(manager);
        binding.chatRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                binding.chatRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendmsgToUser(String inputmessage) {
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(Utils.currentUserId());
        chatroomModel.setLastMessage(inputmessage);
        Utils.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(inputmessage, Utils.currentUserId(), Timestamp.now());
        Utils.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            binding.chatMessageInput.setText("");
                            //sendNotification(message);
                        }
                    }
                });
    }

    void getOrCreateChatroomModel() {
        Utils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(Utils.currentUserId(), getSharedData(ChatActivity.this, getString(R.string.ll_userID), null)),
                            Timestamp.now(),
                            ""
                    );
                    Utils.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }
}