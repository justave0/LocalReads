package com.example.localreads;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localreads.Models.MessageGroup;
import com.google.android.material.card.MaterialCardView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CondensedMessageAdapter extends RecyclerView.Adapter<CondensedMessageAdapter.ViewHolder> {
    Context context;
    ArrayList<MessageGroup> mMessageGroups;

    public CondensedMessageAdapter(ArrayList<MessageGroup> messageGroups, Context context){
        this.context = context;
        mMessageGroups = messageGroups;
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
        MessageGroup messageGroup = mMessageGroups.get(position);
        holder.bind(messageGroup);
    }

    public void clear() {
        mMessageGroups.clear();
        notifyDataSetChanged();
    }

    public void notifyAdapter(ArrayList<MessageGroup> messageGroups){
        mMessageGroups.addAll(messageGroups);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMessageGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCondensedMessagePFP;
        TextView tvCondensedMessageUsernames;
        TextView tvCondensedMessageSnippet;
        TextView tvCondensedMessageTime;
        MaterialCardView cvCondensedMessage;
        ArrayList <ParseUser> parseUsers = new ArrayList<>();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCondensedMessagePFP = itemView.findViewById(R.id.ivCondensedMessagePFP);
            tvCondensedMessageSnippet = itemView.findViewById(R.id.tvCondensedMessageSnippet);
            tvCondensedMessageTime = itemView.findViewById(R.id.tvCondensedMessageTime);
            tvCondensedMessageUsernames = itemView.findViewById(R.id.tvCondensedMesssageUsernames);
            cvCondensedMessage = itemView.findViewById(R.id.cvCondensedMessage);
        }

        public void bind(MessageGroup messageGroup) {
            ParseUser recentUser = queryRecentUser(messageGroup);
            try {
                List<ParseObject> users= messageGroup.getRelation(MessageGroup.KEY_USERS).getQuery().find();
                String usernames = "";
                for (int i = 0; i < users.size(); i ++){
                    if (!((ParseUser)users.get(i)).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                        usernames += ((ParseUser) users.get(i)).getUsername() + ", ";
                    }
                }
                usernames = usernames.replaceAll(", $", "");
                tvCondensedMessageUsernames.setText(usernames);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvCondensedMessageSnippet.setText(messageGroup.getRecentText());
            tvCondensedMessageTime.setText(calculateTimeAgo(messageGroup.getTimeStamp()));
            Glide.with(context).load(recentUser.getParseFile("profilePic").getUrl()).circleCrop().into(ivCondensedMessagePFP);
            cvCondensedMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goDetailedMessage(messageGroup);
                }
            });
        }

        private ParseUser queryRecentUser(MessageGroup messageGroup) {
            ParseQuery messageUsers = messageGroup.getUsers().getQuery();
            messageUsers.setLimit(1);
            messageUsers.addDescendingOrder(MessageGroup.KEY_CREATED_AT);
            try {
                return (ParseUser) messageUsers.find().get(0);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    public void goDetailedMessage(MessageGroup messageGroup) {
        Intent intent = new Intent(context, DetailedMessageActivity.class);
        intent.putExtra("messageGroup", Parcels.wrap(messageGroup));
        context.startActivity(intent);
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
