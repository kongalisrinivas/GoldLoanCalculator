package com.goldloancalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    EditText username, emialId, password, mobilenumber, location;
    String Username, Emailid, Password, Mobilenumber, Location;
    Button createUserr;
    FirebaseAuth fAuth;
    ProgressBar Loading;
    Map<String, Object> userMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.username_txt);
        emialId = findViewById(R.id.emailId_txt);
        password = findViewById(R.id.createpassword_txt);
        mobilenumber = findViewById(R.id.txt_mobilenumber);
        location = findViewById(R.id.location_txt);
        Loading = findViewById(R.id.Loading);
        createUserr =findViewById(R.id.createUser);

        fAuth = FirebaseAuth.getInstance();

        createUserr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMap = new HashMap<>();
                createUserr.setClickable(false);
                Loading.setVisibility(View.VISIBLE);
                Username = username.getText().toString();
                Emailid = emialId.getText().toString();
                Password = password.getText().toString();
                Mobilenumber = mobilenumber.getText().toString();
                Location = location.getText().toString();


                userMap.put("username", Username);
                userMap.put("emailid", Emailid);
                userMap.put("password", Password);
                userMap.put("mobilenumber", Mobilenumber);
                userMap.put("location", Location);
                userMap.put("role","User");

                if (isAllFieldsChecked()) {

                    fAuth.createUserWithEmailAndPassword(Emailid, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = fAuth.getCurrentUser();
                                assert user != null;
                                String id = user.getUid();
                                saveUserData(id);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("UserScreen","Exception "+ e);
                            Toast.makeText(SignupActivity.this, "Failed to Create User", Toast.LENGTH_SHORT).show();
                            Loading.setVisibility(View.GONE);
                            createUserr.setClickable(true);
                        }
                    });

                }
            }

           });
    }
    private boolean isAllFieldsChecked() {

        if (Username.length() == 0) {
            username.setError("This field can't be Empty");
            return false;
        }
        if (Mobilenumber.length() == 0) {
            mobilenumber.setError("Field can't be Empty");
            return false;
        }
        if (Password.length() == 0) {
            password.setError("Field can't be Empty");
            return false;
        }
        if (Emailid.length() == 0) {
            emialId.setError("Field can't be Empty");
            return false;
        }
        if (Location.length() == 0) {
            location.setError("Field can't be Empty");
            return false;
        }

        return true;

    }

    private void saveUserData(String id) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(id)
                .setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                Loading.setVisibility(View.GONE);
                createUserr.setClickable(true);
                Intent i = new Intent(SignupActivity.this, Homepage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fAuth.getCurrentUser().delete();
                Toast.makeText(SignupActivity.this, "Failed to Save User", Toast.LENGTH_SHORT).show();
                createUserr.setClickable(true);
            }
        });
    }
}