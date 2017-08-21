package com.kreaciouser.firebasetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kreaciouser.firebasetest.Model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.display_name_text) TextView displayName;
    @BindView(R.id.status_message_text) TextView statusMessage;
    @BindView(R.id.change_email_button) Button changeEmailButton;
    @BindView(R.id.change_password_button) Button changePasswordButton;
    @BindView(R.id.sign_out_button) Button signOutButton;
    FirebaseAuth auth;
    DatabaseReference firebaseDB;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance().getReference();
        if (auth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        firebaseUser = auth.getCurrentUser();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        firebaseDB.child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    displayName.setText(user.getDisplayName());
                    statusMessage.setText(user.getStatus());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
            }
        });
    }
}
