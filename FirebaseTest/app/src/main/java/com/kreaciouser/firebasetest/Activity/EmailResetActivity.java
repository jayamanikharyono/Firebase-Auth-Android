package com.kreaciouser.firebasetest.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.kreaciouser.firebasetest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmailResetActivity extends AppCompatActivity {
    @BindView(R.id.email_reset_password_button) Button emailResetPasswordButton;
    @BindView(R.id.email_reset_password_text) EditText emailResetPasswordInput;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_reset);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        emailResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.sendPasswordResetEmail(emailResetPasswordInput.getText().toString().trim());
                finish();
            }
        });
    }
}
