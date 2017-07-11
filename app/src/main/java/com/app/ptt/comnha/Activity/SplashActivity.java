package com.app.ptt.comnha.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends BaseActivity {
    TextView dot1, dot2, dot3, dot4, dot5, dot6;
    ImageView imgLogo;
    public static final int MULTIPLE_PERMISSIONS = 10; // cod
    private static int SPLASH_TIME_OUT = 3000;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference dbRef;
    private ValueEventListener userValueListener;
    private User user;

    @Override
    protected void onStart() {
        super.onStart();
        if (MyService.isNetworkAvailable(this)) {
            getUser();
        } else {
            Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
            LoginSession.getInstance().setFirebUser(null);
            LoginSession.getInstance().setUser(null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMainActi();
                }
            }, SPLASH_TIME_OUT);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseDB_path));
        dot1 = (TextView) findViewById(R.id.act_splash_dot1);
        dot2 = (TextView) findViewById(R.id.act_splash_dot2);
        dot3 = (TextView) findViewById(R.id.act_splash_dot3);
        dot4 = (TextView) findViewById(R.id.act_splash_dot4);
        dot5 = (TextView) findViewById(R.id.act_splash_dot5);
        dot6 = (TextView) findViewById(R.id.act_splash_dot6);
        imgLogo = (ImageView) findViewById(R.id.act_splash_imglogo);
        AnimationUtils.getInstance().animateTransTrip(dot1, dot2, dot3, dot4, dot5, dot6);
        AnimationUtils.animateTransAlpha(imgLogo);

    }

    private void getUser() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                try {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        // User is signed in
                        getUserInfo(firebaseUser);
                        Log.d("onAuthStateChanged", "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                    } else {
                        // User is signed out
                        Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
                        LoginSession.getInstance().setFirebUser(null);
                        LoginSession.getInstance().setUser(null);
                        startMainActi();
                    }
                } catch (Exception e) {

                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void getUserInfo(final FirebaseUser firebaseUser) {
        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    user = dataSnapshot.getValue(User.class);
                    String key = firebaseUser.getUid();
                    user.setuID(key);
                    LoginSession.getInstance().setUser(user);
                    LoginSession.getInstance().setFirebUser(firebaseUser);
                    mAuth.removeAuthStateListener(mAuthListener);
                } catch (Exception e) {

                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
                    LoginSession.getInstance().setFirebUser(null);
                    LoginSession.getInstance().setUser(null);
                }
                startMainActi();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + firebaseUser.getUid())
                .addListenerForSingleValueEvent(userValueListener);
    }

    private void startMainActi() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
