package com.example.friendspace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.friendspace.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private Button mButtonLogout, mBtnEditPicture, mBtnChangePassword;
    private TextView mUserName;
    private Toolbar mToolbar;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorage;

    public static final String TAG = "ProfileActivity";
    private static final int SELECT_IMAGE = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfileImage = findViewById(R.id.profile_image);
        mUserName = findViewById(R.id.user_name);

        mButtonLogout = findViewById(R.id.btn_logout);
        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sign out user
                FirebaseAuth.getInstance().signOut();
                // go back to start screen
                startActivity(new Intent(ProfileActivity.this, StartActivity.class));
                finish();
            }
        });

        mBtnEditPicture = findViewById(R.id.btn_edit_picture);
        mBtnEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // display dialog box
                displayDialog();
            }
        });

        mBtnChangePassword = findViewById(R.id.btn_change_password);
        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to ChangePasswordActivity
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });

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

        // initialize Storage Reference
        mStorage = FirebaseStorage.getInstance().getReference("images");
        // initialize Firebase User
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // initialize Database Reference
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                // set user's name
                String fullName = String.format(getResources().getString(R.string.full_name),user.getFirstName(), user.getLastName());
                mUserName.setText(fullName);
                // set user's profile image
                if (!user.getImageURL().equals("default")) {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // change profile image when clicked
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // display dialog box
                displayDialog();
            }
        });
    }

    // open image from android photos
    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_IMAGE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if request code == SELECT_IMAGE, result code is okay and data is not null, upload image
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(ProfileActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            final StorageReference fileReference = mStorage.child(System.currentTimeMillis() + "." +getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "task not successful",Toast.LENGTH_SHORT).show();
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // get url to the uploaded content
                        Uri downloadFile = task.getResult();
                        String stringUri = downloadFile.toString();
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid()).child("imageURL");
                        Log.w(TAG, mDatabaseRef.toString());
                        if (!mDatabaseRef.toString().equals("default")) {
                            deleteImage(mDatabaseRef.toString());
                        }

                        // update user's profile image in database
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", stringUri);
                        mDatabaseRef.updateChildren(hashMap);
                    }
                    else {
                        Toast.makeText(ProfileActivity.this, "Failed to upload new profile image", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteImage(String imagePath) {
        // Create a reference to the file to delete
        StorageReference imageRef = mStorage.child(imagePath);
        Log.w(TAG, imagePath);

        // Delete the file
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.w(TAG, "File deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, "Error: unable to delete file");
            }
        });
    }

    private void displayDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(ProfileActivity.this);
        adb.setCancelable(true)
            .setItems(R.array.edit_photo_options, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    switch(which) {
                        case 0:
                            // open image from android photos
                            selectImage();
                            break;
                        case 1:
                            // remove current photo
                    }
                }
            });
        AlertDialog alert = adb.create();
        alert.show();
    }

}
