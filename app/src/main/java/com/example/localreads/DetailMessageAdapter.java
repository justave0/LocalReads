package com.example.localreads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localreads.Models.Message;
import com.parse.ParseUser;

import java.util.ArrayList;

public class DetailMessageAdapter
    extends RecyclerView.Adapter<DetailMessageAdapter.DetailMessageViewHolder> {
  private final ArrayList<Message> mMessages;
  private final Context mContext;
  private final String mCurrUserId;
  private static final int MESSAGE_OUTGOING = 0;
  private static final int MESSAGE_INCOMING = 1;

  public DetailMessageAdapter(Context context, ArrayList<Message> messages, String currUserId) {
    mMessages = messages;
    mCurrUserId = currUserId;
    mContext = context;
  }

  @NonNull
  @Override
  public DetailMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    if (viewType == MESSAGE_INCOMING) {
      View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
      return new IncomingMessageViewHolder(contactView);
    } else if (viewType == MESSAGE_OUTGOING) {
      View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
      return new OutgoingMessageViewHolder(contactView);
    } else {
      throw new IllegalArgumentException("Unknown view type");
    }
  }

  @Override
  public void onBindViewHolder(@NonNull DetailMessageViewHolder holder, int position) {
    Message message = mMessages.get(position);
    holder.bindMessage(message);
  }

  @Override
  public int getItemCount() {
    return mMessages.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (isMe(position)) {
      return MESSAGE_OUTGOING;
    } else {
      return MESSAGE_INCOMING;
    }
  }

  private boolean isMe(int position) {
    Message message = mMessages.get(position);
    return message.getSender().getObjectId() != null
        && message.getSender().getObjectId().equals(mCurrUserId);
  }

  public abstract class DetailMessageViewHolder extends RecyclerView.ViewHolder {

    public DetailMessageViewHolder(@NonNull View itemView) {
      super(itemView);
    }

    abstract void bindMessage(Message message);
  }

  public class IncomingMessageViewHolder extends DetailMessageViewHolder {
    ImageView ivIncomingMessagePFP;
    TextView tvIncomingMessageBody;
    TextView tvIncomingMessageUsername;

    public IncomingMessageViewHolder(View itemView) {
      super(itemView);
      ivIncomingMessagePFP = (ImageView) itemView.findViewById(R.id.ivOutgoingMessagePFP);
      tvIncomingMessageBody = (TextView) itemView.findViewById(R.id.tvOutgoingMessageBody);
      tvIncomingMessageUsername = (TextView) itemView.findViewById(R.id.tvIncomingMessageUsername);
    }

    @Override
    public void bindMessage(Message message) {
      Glide.with(mContext)
          .load(message.getSender().getParseFile("profilePic").getUrl())
          .circleCrop() // create an effect of a round profile picture
          .into(ivIncomingMessagePFP);
      tvIncomingMessageBody.setText(message.getText());
      tvIncomingMessageUsername.setText(message.getSender().getUsername());
    }
  }

  public class OutgoingMessageViewHolder extends DetailMessageViewHolder {
    ImageView ivOutgoingMessagePFP;
    TextView tvOutgoingMessageBody;

    public OutgoingMessageViewHolder(View itemView) {
      super(itemView);
      ivOutgoingMessagePFP = (ImageView) itemView.findViewById(R.id.ivOutgoingMessagePFP);
      tvOutgoingMessageBody = (TextView) itemView.findViewById(R.id.tvOutgoingMessageBody);
    }

    @Override
    public void bindMessage(Message message) {
      Glide.with(mContext)
          .load(ParseUser.getCurrentUser().getParseFile("profilePic").getUrl())
          .circleCrop() // create an effect of a round profile picture
          .into(ivOutgoingMessagePFP);
      tvOutgoingMessageBody.setText(message.getText());
    }
  }
}
