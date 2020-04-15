package com.example.friendspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.view.Change;

public class ChangePasswordActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mBtnChangePassword;
    private EditText mPassword, mNewPassword, mConfirmPassword;
    private DatabaseReference mReference;
    private static final String TAG = "ChangePassWordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mPassword = findViewById(R.id.password);
        mNewPassword = findViewById(R.id.new_password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mBtnChangePassword = findViewById(R.id.btn_change_password);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Reset Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPassword.getText().toString();
                final String newPassword = mNewPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();

                // check if new password and confirm password match
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Passwords must match", Toast.LENGTH_LONG).show();
                }
                else {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // Get auth credentials from the user for re-authentication
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Password updated");
                                                    Toast.makeText(ChangePasswordActivity.this, "Password updated", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(ChangePasswordActivity.this, ProfileActivity.class));
                                                    finish();
                                                } else {
                                                    Log.d(TAG, "Error password not updated");
                                                    Toast.makeText(ChangePasswordActivity.this, "Error password not updated", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "Error auth failed");
                                        Toast.makeText(ChangePasswordActivity.this, "Authentication failed: incorrect password", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }

            }
        });
    }
}
