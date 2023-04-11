package com.goldloancalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText email_Edt, password_Edt;
    Button login_Btn;
    TextView register_Btn, resetPassword;
    boolean valid;
    FirebaseAuth fAuth;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();

        email_Edt = findViewById(R.id.email_edtTxt);
        password_Edt = findViewById(R.id.password_edtTxt);
        progress = findViewById(R.id.progress);
        register_Btn = findViewById(R.id.register);
        resetPassword = findViewById(R.id.resetPassword);

        login_Btn = findViewById(R.id.login_btn);

        register_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                checkField(email_Edt);
                checkField(password_Edt);

                if (valid) {
                    fAuth.signInWithEmailAndPassword(email_Edt.getText().toString(), password_Edt.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = fAuth.getCurrentUser();
                                assert user != null;
                                String userId = user.getUid();
                                checkUserAccess(userId);
                            } else {
                                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                            progress.setVisibility(View.GONE);

                        }
                    });

                }


            }
        });

    }

    private void checkUserAccess(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // String isAdmin = snapshot.child("isAdmin").getValue().toString();

                /*if (snapshot.getValue() == null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("Email_Id", email_Edt.getText().toString());
                    userInfo.put("PassWord", password_Edt.getText().toString());
                    userInfo.put("isAdmin", 0);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(LoginActivity.this, "Data Collected", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), Homepage.class);
                            startActivity(i);
                            finish();
                        }
                    });
                } else*/
                if (snapshot.child("role").getValue().toString().equals("Admin")) {
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), Settings.class);
                    i.putExtra("isAdmin", true);
                    startActivity(i);
                    finish();

                } else if (snapshot.child("role").getValue().toString().equals("User")) {
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), Homepage.class);
                    i.putExtra("isAdmin",false);
                    startActivity(i);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "User Access Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("Error");
            valid = false;
        } else {
            valid = true;
        }
    }
}