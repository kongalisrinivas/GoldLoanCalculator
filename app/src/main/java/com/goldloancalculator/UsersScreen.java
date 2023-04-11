package com.goldloancalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UsersScreen extends AppCompatActivity {

    Toolbar mtoolbar;
    RecyclerView userslist;
    TextView noUser;
    FloatingActionButton addUser;
    EditText username, emialId, password, mobilenumber, location;
    Button createUserr;
    FirebaseAuth fAuth;

    UserInfo userInfo;
    UsersAdapter usersAdapter;

    String Username, Emailid, Password, Mobilenumber, Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_screen);

        noUser = findViewById(R.id.noUsers);
        userslist = findViewById(R.id.userslist);
        addUser = findViewById(R.id.addUser_btn);

        userInfo = new UserInfo();

        fAuth = FirebaseAuth.getInstance();

        mtoolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolar);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseRecyclerOptions<UserInfo> info = new FirebaseRecyclerOptions.Builder<UserInfo>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Users"), UserInfo.class).build();

        userslist.setLayoutManager(new LinearLayoutManager(UsersScreen.this));
        usersAdapter = new UsersAdapter(info);
        userslist.setAdapter(usersAdapter);
        noUser.setVisibility(View.INVISIBLE);


        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UsersScreen.this);
                View mView = getLayoutInflater().inflate(R.layout.layout_add_user_dialog, null);
                username = mView.findViewById(R.id.username_txt);
                emialId = mView.findViewById(R.id.emailId_txt);
                password = mView.findViewById(R.id.createpassword_txt);
                mobilenumber = mView.findViewById(R.id.txt_mobilenumber);
                location = mView.findViewById(R.id.location_txt);
                createUserr = mView.findViewById(R.id.createUser);

                dialog.setView(mView);

                final AlertDialog alertDialog = dialog.create();
                alertDialog.setCanceledOnTouchOutside(false);

                alertDialog.show();

                createUserr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Username = username.getText().toString();
                        Emailid = emialId.getText().toString();
                        Password = password.getText().toString();
                        Mobilenumber = mobilenumber.getText().toString();
                        Location = location.getText().toString();

                        Map<String, Object> map = new HashMap<>();
                        map.put("username", Username);
                        map.put("emailid", Emailid);
                        map.put("password", Password);
                        map.put("mobilenumber", Mobilenumber);
                        map.put("location", Location);
                        map.put("role","User");

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

                                private void saveUserData(String id) {
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(id)
                                            .setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            userInfo.setUsername(Username);
                                            userInfo.setEmailid(Emailid);
                                            userInfo.setPassword(Password);
                                            userInfo.setMobilenumber(Mobilenumber);
                                            userInfo.setLocation(Location);
                                            alertDialog.dismiss();

                                            Toast.makeText(UsersScreen.this, "Created User and Saved", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(UsersScreen.this, "Failed to Save User", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("UserScreen","Exception "+ e);
                                    Toast.makeText(UsersScreen.this, "Failed to Create User", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
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
                });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        usersAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usersAdapter.stopListening();
    }
}