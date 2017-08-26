package com.kreaciouser.firebasetest.Model;

/**
 * Created by kreaciouser on 8/21/17.
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class User {
    public String uid;
    public String imagePhotoUrl;
    public String displayName;
    public String email;
    public String status;

    public User(){

    }

    public User(String uid, String imagePhotoUrl, String displayName, String email) {
        this.uid = uid;
        this.imagePhotoUrl = imagePhotoUrl;
        this.displayName = displayName;
        this.email = email;
    }

    public User(String uid, String imagePhotoUrl, String displayName, String email, String status) {
        this.uid = uid;
        this.imagePhotoUrl = imagePhotoUrl;
        this.displayName = displayName;
        this.email = email;
        this.status = status;
    }

}
