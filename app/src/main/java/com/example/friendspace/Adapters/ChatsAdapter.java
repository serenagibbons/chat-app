package com.example.friendspace.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendspace.Model.Chat;
import com.example.friendspace.Model.User;
import com.example.friendspace.R;
import com.example.friendspace.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<User> mPeople;
    private List<User> mPeopleFull;

    private String mLastMessage;
    private List<DatabaseReference> mChatList;

    // get current user
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    // constructor
    public ChatsAdapter(Context mContext, List<User> mPeople) {
        this.mContext = mContext;
        this.mPeople = mPeople;
        // create a copy of mPeople to always contain full list of people
        mPeopleFull = new ArrayList<>();
        mPeopleFull.addAll(mPeople);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
        return new ChatsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mPeople.get(position);
        // set user's full name
        String fullName = String.format(mContext.getResources().getString(R.string.full_name), user.getFirstName(), user.getLastName());
        holder.name.setText(fullName);
        // set user's profile image
        if (!user.getImageURL().equals("default")) {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profileImage);
        }

        // get last message
        getLastMessage(user.getId(), holder.lastMessage);

        // mark as read or unread
        mChatList = new ArrayList<>();
        getReadStatus(user.getId(), mChatList, holder.unreadMessage);

        // item OnClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userID", user.getId());
                // if message is unread, mark as read
                if (holder.unreadMessage.getVisibility() == View.VISIBLE) {
                    markAsRead(mChatList);
                }
//                    markAsRead(user.getId(), holder.unreadMessage);
                //}
                // open UserActivity on clicking recycler view item
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPeople.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<User> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(mPeopleFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    // iterate through full list of people
                    for (User user : mPeopleFull) {
                        if (user.getLastName().toLowerCase().contains(filterPattern)
                                || user.getFirstName().toLowerCase().contains(filterPattern)) {
                            // if user first or last name contains filterPattern add it to filteredList
                            filteredList.add(user);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mPeople.clear();
                mPeople.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, lastMessage;
        CircleImageView profileImage, unreadMessage;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.last_message);
            profileImage = itemView.findViewById(R.id.profile_image);
            unreadMessage = itemView.findViewById(R.id.unread_message);
        }
    }

    private void getReadStatus(final String userID, final List<DatabaseReference> chatList, final CircleImageView unreadMessage) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    // if the receiver is the current user and sender is the other user in the message
                    if (chat.getReceiver().equals(mFirebaseUser.getUid()) && chat.getSender().equals(userID)) {
                        // if chat is unread set view to visible, else set to invisible
                        chatList.add(snapshot.getRef());
                        unreadMessage.setVisibility(chat.getIsUnread() ? View.VISIBLE : View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void markAsRead(List<DatabaseReference> chatRefs) {
        for (DatabaseReference chatRef : chatRefs) {
            // update chat isUnread to false (read)
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("isUnread", false);
            chatRef.updateChildren(hashMap);
        }
    }

    /*private void markAsRead(final String userID, final CircleImageView unreadMessage) {
        // FIX THIS CODE
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    // if the receiver is the current user and sender is the other user in the message
                    if (chat.getReceiver().equals(mFirebaseUser.getUid()) && chat.getSender().equals(userID)) {
                        // update chat isUnread to false (read)
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isUnread", false);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    private void getLastMessage(final String userID, final TextView lastMessage) {
        mLastMessage = "";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(mFirebaseUser.getUid()) && chat.getSender().equals(userID)
                            || chat.getReceiver().equals(userID) && chat.getSender().equals(mFirebaseUser.getUid())) {
                        mLastMessage = chat.getMessage();
                    }
                }
                lastMessage.setText(mLastMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
