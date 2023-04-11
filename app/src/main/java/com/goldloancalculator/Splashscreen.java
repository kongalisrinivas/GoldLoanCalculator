package com.goldloancalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splashscreen extends AppCompatActivity {
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
         mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mUser!=null){
                    String id = mUser.getUid();
                    checkUserAccess(id);
                } else {
                    Intent mainIntent = new Intent(Splashscreen.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);

    }

    private void checkUserAccess(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("role").getValue().toString().equals("Admin")) {
                    Intent i = new Intent(Splashscreen.this, Settings.class);
                    i.putExtra("Admin", "Admin");
                    startActivity(i);
                    finish();

                } else if (snapshot.child("role").getValue().toString().equals("User")) {
                    Intent i = new Intent(Splashscreen.this, Homepage.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Splashscreen.this, "Database Issue " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}