package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.showProgressDialog;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.ChatActivity;
import com.app.giftxchange.adapter.FirebaseMessageListAdapter;
import com.app.giftxchange.model.MessageModel;
import com.app.giftxchange.databinding.FragmentChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ChatFragment extends Fragment {

    String userId, clientId;
    String msg;
    private ArrayList<MessageModel> clientList;
    private FragmentChatBinding binding;
    FirebaseMessageListAdapter messageListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(getLayoutInflater());

        // Inflate the layout for this fragment
        getDataFromIntent();

        clientList = new ArrayList<>();
        messageListAdapter = new FirebaseMessageListAdapter(clientList, getActivity(), userId, clientId);
        binding.rvMsgList.setAdapter(messageListAdapter);
        binding.rvMsgList.setLayoutManager(new LinearLayoutManager(getActivity()));

        showProgressDialog(getActivity(), getString(R.string.please_wait));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("message");

        databaseReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hideProgressDialog(getActivity());
                    for (DataSnapshot mainKeyChild : dataSnapshot.getChildren()) {
                        String mainKey = mainKeyChild.getKey();
                        Log.d("TAG", "onDataChange: ............................1............." + mainKey);
                        System.out.println("MainKey: " + mainKey);
                        String[] parts = mainKey.split("\\|");
                        if (Objects.equals(parts[0], userId)) {
                            clientList.add(new MessageModel(parts[0], parts[1], "User"));
                        }
                        if (Objects.equals(parts[1], userId)) {
                            clientList.add(new MessageModel(parts[0], parts[1], "Client"));
                        }
                    }
                    Log.d("TAG", "onDataChange: ............................2............." + clientList.size());
                    messageListAdapter.notifyDataSetChanged();
                } else {
                    hideProgressDialog(getActivity());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                hideProgressDialog(getActivity());
            }
        });

        return binding.getRoot();

    }


    private void getDataFromIntent() {
        String currentuserID = getSharedData(getContext(), getString(R.string.key_userid), null);

        msg = "Hello";
        userId = currentuserID;
        clientId = null;
    }
}