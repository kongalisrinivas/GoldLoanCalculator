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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email_Edt;
    Button forgot_btn;
    TextView backtoLogin;
    ProgressBar progress;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        fAuth = FirebaseAuth.getInstance();
        email_Edt = findViewById(R.id.email_edtTxt);

        progress = findViewById(R.id.progress);
        forgot_btn = findViewById(R.id.forgotbtn);
        backtoLogin = findViewById(R.id.backToLogin);

        backtoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                finish();
            }
        });

        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.sendPasswordResetEmail(email_Edt.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        );
            }
        });
    }
}