package com.kreaciouser.firebasetest.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kreaciouser.firebasetest.Model.User;
import com.kreaciouser.firebasetest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.display_name_input) EditText displayNameInput;
    @BindView(R.id.status_message_input) EditText statusMessageInput;
    @BindView(R.id.email_input) EditText emailInput;
    @BindView(R.id.password_input) EditText passwordInput;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.register_button) Button registerButton;
    FirebaseAuth auth;
    DatabaseReference firebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance().getReference();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(displayNameInput.getText().toString().isEmpty() || statusMessageInput.getText().toString().isEmpty()
                    || emailInput.getText().toString().isEmpty() || passwordInput.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Provide Data", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(passwordInput.getText().length() < 6){
                    Toast.makeText(getApplicationContext(), "Password Minimum 6 Chars", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                final String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                Task<AuthResult> authResultTask = auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String uid = auth.getCurrentUser().getUid();
                            String displayName = displayNameInput.getText().toString().trim();
                            String status =  statusMessageInput.getText().toString().trim();
                            firebaseDB.child("users").child(uid).setValue(new User(uid, displayName, email, status), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    progressBar.setVisibility(View.GONE);
                                    onBackPressed();
                                }
                            });
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Register Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
