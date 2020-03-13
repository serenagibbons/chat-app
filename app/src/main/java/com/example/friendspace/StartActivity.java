package com.example.friendspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button mButtonLogin, mButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mButtonLogin = findViewById(R.id.btn_login1);
        mButtonRegister = findViewById(R.id.btn_register1);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to log in screen
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to registration screen
                startActivity(new Intent(StartActivity.this, RegistrationActivity.class));
            }
        });
    }
}
