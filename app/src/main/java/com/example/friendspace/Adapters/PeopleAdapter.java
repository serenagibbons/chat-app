package com.example.friendspace.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendspace.Model.User;
import com.example.friendspace.R;
import com.example.friendspace.ChatActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mPeople;

    // constructor
    public PeopleAdapter(Context mContext, List<User> mPeople) {
        this.mContext = mContext;
        this.mPeople = mPeople;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.people_item, parent, false);
        return new PeopleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mPeople.get(position);
        // set user's full name
        String fullName = String.format(mContext.getResources().getString(R.string.full_name), user.getFirstName(), user.getLastName());
        holder.name.setText(fullName);
        // set user's profile image
        if (!user.getImageURL().equals("default")) {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profileImage);
        }

        // open UserActivity on clicking recycler view item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userID", user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPeople.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CircleImageView profileImage;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}
