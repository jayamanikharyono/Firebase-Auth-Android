package com.kreaciouser.firebasetest.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kreaciouser.firebasetest.Model.User;
import com.kreaciouser.firebasetest.R;

import java.security.AuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.display_name_text) TextView displayName;
    @BindView(R.id.status_message_text) TextView statusMessage;
    @BindView(R.id.change_password_button) Button changePasswordButton;
    @BindView(R.id.sign_out_button) Button signOutButton;
    @BindView(R.id.main_toolbar) Toolbar mainToolbar;
    FirebaseAuth auth;
    DatabaseReference firebaseDB;
    FirebaseUser firebaseUser;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance().getReference();
        if (auth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        firebaseUser = auth.getCurrentUser();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mainToolbar);
        mainToolbar.setTitle("@string/app_title");
        try{
            firebaseDB.child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        user = dataSnapshot.getValue(User.class);
                        displayName.setText(user.displayName);
                        statusMessage.setText(user.status);
                        progressBar.setVisibility(View.GONE);
                    }
                    catch (NullPointerException npe){

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (NullPointerException npe){

        }
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                finish();
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Provider ID", auth.getCurrentUser().getProviders().toString());
                if(auth.getCurrentUser().getProviders().toString().equals("[facebook.com]")){
                    LoginManager.getInstance().logOut();
                }
                else if (auth.getCurrentUser().getProviderId().equals("google")){

                }
                auth.signOut();
                onBackPressed();
            }
        });

    }
}
