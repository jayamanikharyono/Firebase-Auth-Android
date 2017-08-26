package com.kreaciouser.firebasetest.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kreaciouser.firebasetest.Model.User;
import com.kreaciouser.firebasetest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadPoolExecutor;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.email_text) EditText emailInput;
    @BindView(R.id.password_text) EditText passwordInput;
    @BindView(R.id.login_button) Button loginButton;
    @BindView(R.id.reset_password_button) Button resetPasswordButton;
    @BindView(R.id.register_button) Button registerButton;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.facebook_login_button) LoginButton facebookLoginButton;
    FirebaseAuth auth;
    DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference();
    private CallbackManager callbackManager;
    public static final String TAG = "LoginActivity";
    private String uid, profilePicture, firstName, middleName, lastName, email;
    boolean exits = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        try{
            if(auth.getCurrentUser() != null){
                startActivity(new Intent(this, MainActivity.class));
            }
        }
        catch (NullPointerException npe){

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions("email");
        facebookLoginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.d(TAG, "User ID : "+loginResult.getAccessToken().getUserId() + " \n Access Token : "+loginResult.getAccessToken().getToken());
                        handleFacebookLogin(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailInput.getText().toString().isEmpty() || passwordInput.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Provide Email and Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                String email = emailInput.getText().toString();
                final String password =  passwordInput.getText().toString();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext() , MainActivity.class));
                            finish();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            emailInput.setError("");
                            passwordInput.setError("");
                        }
                    }
                });
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EmailResetActivity.class));
            }
        });
    }

    private void handleFacebookLogin(final AccessToken token){
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential authCredential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(authCredential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if (!checkExistingUser()){
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GraphRequest graphRequest = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(JSONObject object, GraphResponse response) {
                                                Log.i("TAG", object.toString());
                                                try {
                                                    uid = object.getString("id");
                                                    //profilePicture = object.getString("https://graph.facebook.com/" + uid + "/picture?width=500&height=500");
                                                    firstName = object.getString("first_name");
                                                    middleName = object.getString("middle_name");
                                                    lastName = object.getString("last_name");
                                                    email = object.getString("email");
                                                    Log.i("Data : -----------", uid + profilePicture + firstName + middleName + lastName + email);
                                                } catch (JSONException jsone) {
                                                    Log.i("Error JsonObject : ", jsone.getMessage());
                                                }
                                            }
                                        });
                                        Bundle parameters = new Bundle();
                                        parameters.putString("fields", "id, first_name, middle_name, last_name, email");
                                        graphRequest.setParameters(parameters);
                                        graphRequest.executeAndWait();
                                    }
                                });
                                thread.start();
                                try {
                                    thread.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                String displayName =  firstName + " " + middleName+" "+lastName;
                                User user = new User(uid, profilePicture, displayName, email, "");
                                fireDB.child("users").child(auth.getCurrentUser().getUid()).setValue( user, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(getApplicationContext() , MainActivity.class));
                                    }
                                });
                            }
                            else {
                                startActivity(new Intent(getApplicationContext() , MainActivity.class));
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            emailInput.setError("Emaill incorect");
                            passwordInput.setError("Pass incorect");
                        }
                    }
                });
    }
    public boolean checkExistingUser(){
        fireDB.child("users/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(auth.getCurrentUser().getUid()))
                    exits = true;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return exits;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}
