package com.example.localreads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localreads.Models.Message;

import java.util.ArrayList;

public class CondensedMessageAdapter extends RecyclerView.Adapter<CondensedMessageAdapter.ViewHolder> {
    Context context;
    ArrayList<Message> mMessages;

    public CondensedMessageAdapter(ArrayList<Message> messages, Context context){
        this.context = context;
        mMessages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
