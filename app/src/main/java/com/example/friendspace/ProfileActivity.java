package com.example.friendspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.friendspace.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private TextView mUserName;
    private Toolbar mToolbar;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfileImage = findViewById(R.id.profile_image);
        mUserName = findViewById(R.id.user_name);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                // set user's name
                String fullName = String.format(getResources().getString(R.string.full_name),user.getFirstName(), user.getLastName());
                mUserName.setText(fullName);
                // set user's profile image
                if (!user.getImageURL().equals("default")) {
                    Glide.with(ProfileActivity.this).load(user.getImageURL()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
