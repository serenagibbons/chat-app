package com.example.friendspace.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendspace.Model.Chat;
import com.example.friendspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_RECEIVED = 0;
    private static final int MSG_TYPE_SENT = 1;
    private Context mContext;
    private List<Chat> mChats;

    private FirebaseUser mFirebaseUser;

    // constructor
    public MessageAdapter(Context mContext, List<Chat> mChats) {
        this.mContext = mContext;
        this.mChats = mChats;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RECEIVED) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_received, parent, false);
        }
        else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_sent, parent, false);
        }
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        holder.message_text.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView message_text;

        ViewHolder(View itemView) {
            super(itemView);

            message_text = itemView.findViewById(R.id.message_text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mChats.get(position).getSender().equals(mFirebaseUser.getUid())) {
            return MSG_TYPE_SENT;
        }
        return MSG_TYPE_RECEIVED;
    }
}
