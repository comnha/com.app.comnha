package com.app.ptt.comnha.FireBase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.Account;
import com.app.ptt.comnha.Interfaces.Transactions;
import com.app.ptt.comnha.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by PTT on 9/16/2016.
 */
public class Users implements Transactions {
    private String ho;
    private String ten;
    private String tenlot;
    private String email;
    private String password;
    private String confirmPass;
    private String birth;
    private String username;

    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    boolean role;
    private final Context suContext;
    private Firebase suRef;

    public Users(Context suContext) {
        this.suContext = suContext;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setTenlot(String tenlot) {
        this.tenlot = tenlot;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    @Override
    public void setupFirebase() {
        Firebase.setAndroidContext(suContext);
        suRef = new Firebase("https://comnha-c03b5.firebaseio.com/");
    }

    @Override
    public boolean createNew() {
        setupFirebase();
        if (!password.equals(confirmPass)) {
            Toast.makeText(suContext, "Mật khẩu xác nhận không đúng!!!", Toast.LENGTH_SHORT).show();
        } else {
            final FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user=task.getResult().getUser();
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        user.updateProfile(profileUpdate);
                        String userID = task.getResult().getUser().getUid();
                        Account newAccount = new Account();
                        newAccount.setHo(ho);
                        newAccount.setTen(ten);
                        newAccount.setTenlot(tenlot);
                        newAccount.setPassword(password);
                        newAccount.setBirth(birth);
                        newAccount.setReportCount(0);
                        newAccount.setRole(false);
                        newAccount.setId(userID);
                        newAccount.setUsername(username);
                        suRef.child("Users/" + userID).setValue(newAccount, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Toast.makeText(suContext, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(suContext, R.string.text_signup_successful, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Log.e("error", task.getException().toString());
                        Toast.makeText(suContext, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }
}
