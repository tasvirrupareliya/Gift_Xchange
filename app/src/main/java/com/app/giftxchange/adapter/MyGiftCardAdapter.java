package com.app.giftxchange.adapter;

import static com.app.giftxchange.utils.Utils.setToast;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.giftxchange.activity.GiftcardView;
import com.app.giftxchange.databinding.ItemMygiftviewBinding;
import com.app.giftxchange.model.MyGiftCards;

import java.util.List;

public class MyGiftCardAdapter extends RecyclerView.Adapter<MyGiftCardAdapter.ViewHolder> {
    public List<MyGiftCards> itemList;

    public MyGiftCardAdapter(List<MyGiftCards> itemList) {
        this.itemList = itemList;
    }

    public MyGiftCardAdapter() {

    }

    @NonNull
    @Override
    public MyGiftCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMygiftviewBinding binding = ItemMygiftviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyGiftCardAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyGiftCardAdapter.ViewHolder holder, int position) {
        MyGiftCards item = itemList.get(position);

        holder.binding.textCardNumber.setText(item.getCardNumber());
        holder.binding.textCVVValue.setText(item.getCardCVV());
        holder.binding.textCardHolderName.setText(item.getCardName());
        holder.binding.textExpiryDateValue.setText(item.getCardExpiryDate());
        holder.binding.valueTextView.setText("Value : $" + item.getCardAmount());

        holder.binding.copyicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClipboard("", holder.binding.textCardNumber.getText().toString(), view.getContext());
            }
        });
    }

    private void copyToClipboard(String label, String text, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        setToast(context, "Card Number copied to clipboard");
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMygiftviewBinding binding; // Use the View Binding object

        public ViewHolder(ItemMygiftviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
