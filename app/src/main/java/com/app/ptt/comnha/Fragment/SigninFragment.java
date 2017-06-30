package com.app.ptt.comnha.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.ptt.comnha.Activity.AdapterActivity;
import com.app.ptt.comnha.Activity.MainActivity;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;
import static com.app.ptt.comnha.R.id.btn_siFrg_signin;
import static com.app.ptt.comnha.Utils.AppUtils.isEqualsNull;
import static com.app.ptt.comnha.Utils.AppUtils.showSnackbar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends BaseFragment implements View.OnClickListener {
    private EditText edt_email, edt_pass;
    private User user;FirebaseUser firebaseUser;

    private Button btn_signin;
    private TextView txtV_signup;
    int signinfromStoreDe = -1;

    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private ValueEventListener userValueListener;
    public SigninFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        anhXa(view);
        signinfromStoreDe = this.getArguments().getInt("signinfromStoreDe");
        Bundle args = getArguments();
        if(args!=null && args.getString("name")!=null&& args.getString("pass")!=null){
            edt_email.setText(args.getString("name").toString());
            edt_pass.setText(args.getString("pass").toString());
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.text_signup_successful));
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }

    private void anhXa(final View view) {
        edt_email = (EditText) view.findViewById(R.id.edt_siFrg_username);
        edt_pass = (EditText) view.findViewById(R.id.edt_siFrg_password);
        txtV_signup = (TextView) view.findViewById(R.id.txtV_signup);
        btn_signin = (Button) view.findViewById(btn_siFrg_signin);
        txtV_signup.setOnClickListener(this);
        btn_signin.setOnClickListener(this);
        edt_email.requestFocus();
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtV_signup:
                if (isNetworkConnected) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), AdapterActivity.class);
                    intent.putExtra(getActivity().getResources().getString(R.string.fragment_CODE),
                            getActivity().getResources().getString(R.string.frg_signup_CODE));
                    intent.putExtra("email", AppUtils.getText(edt_email));
                    intent.putExtra("pass", AppUtils.getText(edt_pass));
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, Const.INTENT_KEY_SIGN_UP);
                } else {
                    showSnackbar(getActivity(), getView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_SHORT);
                }
                break;
            case R.id.btn_siFrg_signin:
                if (isNetworkConnected) {
                    doSignin(view);
                } else
                    showSnackbar(getActivity(), getView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_SHORT);

                break;
//            case R.id.btn_siFrg_exit:
//                getActivity().finish();
//                break;
//            case R.id.butt_siFrg_loginFB:
//                if (isConnected) {
//                } else
//                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.butt_siFrg_loginGmail:
//                if (isConnected) {
//                } else
//                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.txt_siFrg_forgotPass:
//                if (isConnected) {
//                } else
//                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
//                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.INTENT_KEY_SIGN_UP) {
            if (resultCode == Activity.RESULT_OK) {
                if (!AppUtils.isTextEqualsNull(data.getStringExtra(Const.INTENT_KEY_EMAIL).toString())
                        && !AppUtils.isTextEqualsNull(data.getStringExtra(Const.INTENT_KEY_PASSWORD).toString())) {
                    AppUtils.showSnackbarWithoutButton(getView(), getString(R.string.text_signup_successful));
                    edt_email.setText(data.getStringExtra(Const.INTENT_KEY_EMAIL).toString());
                    edt_pass.setText(data.getStringExtra(Const.INTENT_KEY_PASSWORD).toString());
                }
            }
        }
    }

    void doSignin(final View view) {
        showProgressDialog(getActivity(), getString(R.string.text_signin), getString(R.string.txt_plzwait));
        if (checkInput(view)) {
            auth.signInWithEmailAndPassword(edt_email.getText().toString(),
                    edt_pass.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:onComplete", task.getException());
                                AppUtils.showSnackbarWithoutButton(view, getString(R.string.text_signin_fail));
                            } else {
                              getUser();
                            }
                            closeDialog();

                        }
                    });
        }

    }
    private void getUser() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    getUserInfo(firebaseUser);
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                } else {
                    AppUtils.showSnackbarWithoutButton(getView(), getString(R.string.text_signin_fail));
                    // User is signed out
                }
                if (mAuthListener != null) {
                    mAuth.removeAuthStateListener(mAuthListener);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

    }
    boolean checkInput(View view) {
        if (isEqualsNull(edt_email)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_noemail));
            edt_email.requestFocus();
            return false;
        }
        if (!isValidEmailAddress(AppUtils.getText(edt_email))) {
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
    private void getUserInfo(final FirebaseUser firebaseUser) {

        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    user = dataSnapshot.getValue(User.class);
                    String key = firebaseUser.getUid();
                    user.setuID(key);
                }catch (Exception e){
                    user=null;
                }
//                mAuth.removeAuthStateListener(mAuthListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + firebaseUser.getUid())
                .addListenerForSingleValueEvent(userValueListener);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(null==user){
                    deleteUser(firebaseUser);
                    auth.signOut();
                }else{
                    LoginSession.getInstance().setUser(null);
                    LoginSession.getInstance().setFirebUser(null);
                    Intent i=new Intent(getActivity(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (signinfromStoreDe == 1) {
                        getActivity().setResult(Activity.RESULT_OK);
                    }
                    getActivity().finish();
                    getActivity().startActivity(i);
                }
                if (mAuthListener != null) {
                    auth.removeAuthStateListener(mAuthListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void deleteUser(FirebaseUser firebaseUser){
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               AppUtils.showSnackbarWithoutButton(getView(),"Tài khoản đã bị xóa");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppUtils.showSnackbarWithoutButton(getView(),"Tài khoản đã bị xóa");
            }
        });
    }

}
