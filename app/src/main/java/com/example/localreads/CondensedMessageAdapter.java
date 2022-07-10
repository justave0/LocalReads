package com.example.localreads;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localreads.Models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View condensedMessage = inflater.inflate(R.layout.item_condensed_message, parent, false);

        // Return a new holder instance
        CondensedMessageAdapter.ViewHolder viewHolder = new CondensedMessageAdapter.ViewHolder(condensedMessage);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.bind(message);
    }

    public void clear() {
        mMessages.clear();
        notifyDataSetChanged();
    }

    public void notifyAdapter(ArrayList<Message> messages){
        mMessages.addAll(messages);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCondensedMessagePFP;
        TextView tvCondensedMessageUsernames;
        TextView tvCondensedMessageSnippet;
        TextView tvCondensedMessageTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCondensedMessagePFP = itemView.findViewById(R.id.ivCondensedMessagePFP);
            tvCondensedMessageSnippet = itemView.findViewById(R.id.tvCondensedMessageSnippet);
            tvCondensedMessageTime = itemView.findViewById(R.id.tvCondensedMessageTime);
            tvCondensedMessageUsernames = itemView.findViewById(R.id.tvCondensedMesssageUsernames);
        }

        public void bind(Message message) {
            queryUsers(message);
            tvCondensedMessageSnippet.setText(message.getText());
            tvCondensedMessageTime.setText(calculateTimeAgo(message.getCreatedAt()));
        }

        private void queryUsers(Message message) {
            ParseQuery messageQuery = message.getRelation(Message.KEY_USERS).getQuery();
            messageQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    ArrayList<String> usernames = new ArrayList<>();
                    for (int i = 0; i< objects.size(); i++){
                        usernames.add(objects.get(i).getUsername());
                    }
                    usernames.removeIf(username -> username.equals(ParseUser.getCurrentUser().getUsername()));
                    tvCondensedMessageUsernames.setText(usernames.toString());
                    Glide.with(context).load(objects.get(0).getParseFile("profilePic").getUrl())
                            .circleCrop().into(ivCondensedMessagePFP);
                }
            });
        }
    }

    //Helper function for timestamp
    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
