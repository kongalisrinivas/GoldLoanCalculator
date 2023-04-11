package com.goldloancalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    TextView txtAddbank, txtAdduser;
    Button logoutbtn;
    FirebaseAuth mAuth;
    Toolbar mtoolbar;

    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);//Settings
        Bundle extras = getIntent().getExtras();
        isAdmin = extras.getBoolean("isAdmin");

        txtAddbank = findViewById(R.id.txt_addbank);
        txtAdduser = findViewById(R.id.txt_adduser);
        logoutbtn = findViewById(R.id.logout_btn);

        mAuth = FirebaseAuth.getInstance();
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);


        txtAddbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addbank = new Intent(Settings.this, Addbank.class);
                addbank.putExtra("role", isAdmin);
                startActivity(addbank);
            }
        });
        txtAdduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adduser = new Intent(Settings.this, UsersScreen.class);
                startActivity(adduser);
            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(Settings.this, LoginActivity.class));
                finish();
            }
        });
    }
}