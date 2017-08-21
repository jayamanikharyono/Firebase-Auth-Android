package com.kreaciouser.firebasetest.Model;

/**
 * Created by kreaciouser on 8/21/17.
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
*/
public class User {
    private String uid;
    private String displayName;
    private String email;
    private String status;

    public User(){

    }

    public User(String uid, String displayName, String email, String status) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
