package com.app.ptt.comnha.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;
import static com.app.ptt.comnha.R.id.btn_siFrg_signin;
import static com.app.ptt.comnha.Utils.AppUtils.isEqualsNull;
import static com.app.ptt.comnha.Utils.AppUtils.showSnackbar;

/**
 * Created by ciqaz on 22/07/2017.
 */

public class SignInActivity extends BaseActivity implements View.OnClickListener {
    private EditText edt_email, edt_pass;
    private User user=null;
    private boolean isLogined;
    FirebaseUser firebaseUser;

    private Button btn_signin;
    private TextView txtV_signup;
    int signinfromStoreDe = -1,signinfromFoodDe=-1;

    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private ValueEventListener userValueListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signin);
        anhXa();
    }
    private void anhXa() {
        edt_email = (EditText) findViewById(R.id.edt_siFrg_username);
        edt_pass = (EditText)findViewById(R.id.edt_siFrg_password);
        txtV_signup = (TextView)findViewById(R.id.txtV_signup);
        btn_signin = (Button) findViewById(btn_siFrg_signin);
        txtV_signup.setOnClickListener(this);
        btn_signin.setOnClickListener(this);
        edt_email.requestFocus();
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
    }
    public void signInOk(String email,String pass){
        if(!TextUtils.isEmpty(email) &&!TextUtils.isEmpty(pass)){
            AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.text_signup_successful));
            edt_email.setText(email);
            edt_pass.setText(pass);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtV_signup:
                if (MyService.isNetworkAvailable(this)) {
                    Intent intent = new Intent(this, SignUpActivity.class);
                    if(!TextUtils.isEmpty(edt_email.getText())&&!TextUtils.isEmpty(edt_pass.getText())) {
                        intent.putExtra("email", AppUtils.getText(edt_email));
                        intent.putExtra("pass", AppUtils.getText(edt_pass));
                    }
                    startActivityForResult(intent,Const.INTENT_KEY_SIGN_UP);
                } else {
                    showSnackbar(this, getWindow().getDecorView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_SHORT);
                }
                break;
            case R.id.btn_siFrg_signin:
                if (MyService.isNetworkAvailable(this)) {
                    checkStatusUser(edt_email.getText().toString().trim());
                } else {
                    showSnackbar(this, getWindow().getDecorView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_SHORT);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Const.INTENT_KEY_SIGN_UP){
            if(resultCode==RESULT_OK){
                String email,pass;

                if(data.getStringExtra(Const.INTENT_KEY_EMAIL)!=null &&data.getStringExtra(Const.INTENT_KEY_PASSWORD)!=null){
                    pass=data.getStringExtra(Const.INTENT_KEY_PASSWORD);
                    email=data.getStringExtra(Const.INTENT_KEY_EMAIL);
                    signInOk(email,pass);
                }

            }
        }
    }

    void doSignin(final User user) {
        showProgressDialog(getString(R.string.text_signin), getString(R.string.txt_plzwait));
        if (checkInput(getWindow().getDecorView())) {
            mAuth.signInWithEmailAndPassword(edt_email.getText().toString(),
                    edt_pass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:onComplete", task.getException());
                                AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.text_signin_fail));
                            } else {
                                getUser(user);
                            }
                            closeDialog();

                        }
                    });
        }else{
            closeDialog();
        }

    }

    private void getUser(final User user) {
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // User is signed in
            LoginSession.getInstance().setUser(user);
            LoginSession.getInstance().setFirebUser(firebaseUser);
                setResult(RESULT_OK);
                finish();
        } else {
            AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.text_signin_fail));
            // User is signed out
        }


    }

    boolean checkInput(View view) {
        if (isEqualsNull(edt_email)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_noemail));
            edt_email.requestFocus();
            return false;
        }
        if (!AppUtils. isValidEmailAddress(AppUtils.getText(edt_email))) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_notemail));
            edt_email.requestFocus();
            return false;
        }
        if (isEqualsNull(edt_pass)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_nopass));
            edt_pass.requestFocus();
            return false;
        }
        return true;
    }
    private void checkStatusUser(String email){

        try {
            userValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            String key = item.getKey();
                            user = item.getValue(User.class);
                            user.setuID(key);
                            if (!user.isStatus() && user.getRole() == 0) {
                                AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.txt_notilockacc));
                            } else {
                                doSignin(user);
                            }
                        }}
                    else{
                            AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.txt_signinfail));
                        }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            dbRef.child(getString(R.string.users_CODE))
                    .orderByChild("email")
                    .equalTo(email)
                    .addListenerForSingleValueEvent(userValueListener);
        }catch (Exception e){
            AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.txt_signinfail));
        }
    }
}
