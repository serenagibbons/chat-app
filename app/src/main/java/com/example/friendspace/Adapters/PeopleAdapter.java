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
import com.example.friendspace.PeopleDialogActivity;
import com.example.friendspace.R;
import com.example.friendspace.ChatActivity;
import com.example.friendspace.UserActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<User> mPeople;
    private List<User> mPeopleFull;

    // constructor
    public PeopleAdapter(Context mContext, List<User> mPeople) {
        this.mContext = mContext;
        this.mPeople = mPeople;
        // create a copy of mPeople to always contain full list of people
        mPeopleFull = new ArrayList<>();
        mPeopleFull.addAll(mPeople);
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

        // open ChatActivity on clicking recycler view item
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
        CircleImageView profileImage;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.last_message);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}
