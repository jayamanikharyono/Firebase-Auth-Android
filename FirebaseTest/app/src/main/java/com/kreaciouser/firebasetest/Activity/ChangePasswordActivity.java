package com.kreaciouser.firebasetest.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kreaciouser.firebasetest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.change_password) Button changePassword;
    @BindView(R.id.new_password_input) EditText newPasswordInput;
    @BindView(R.id.new_confirm_password_input) EditText newConfirmPasswordInput;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newPasswordInput.getText().toString().isEmpty() || newConfirmPasswordInput.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please provide New Password", Toast.LENGTH_LONG).show();
                }
                if(newPasswordInput.getText().length() < 6 || newConfirmPasswordInput.getText().length() < 6){
                    Toast.makeText(getApplicationContext(), "Minimum password 6 chars", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPasswordInput.getText().toString().equals(newConfirmPasswordInput.getText().toString())){
                    auth.getCurrentUser().updatePassword(newPasswordInput.getText().toString().trim());
                }
                onBackPressed();
            }
        });
    }
}
