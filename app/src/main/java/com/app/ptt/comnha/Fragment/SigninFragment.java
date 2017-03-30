package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.Adapter2Activity;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment implements View.OnClickListener {
    private static final String LOG = SigninFragment.class.getSimpleName();
    private EditText edt_email, edt_pass;
    private Button butt_loginFB, butt_loginGmail, btn_signin, btn_exit;
    private TextView txt_forgotPass, txtV_signup;
    private FirebaseAuth mAuth;
    private FloatingActionButton fab_signup;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    android.support.design.widget.Snackbar snackbar;
    ProgressDialog mprogressDialog;
    boolean isConnected = true;
    IntentFilter mIntentFilter;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG + ".onReceive form Service", "isConnected= " + intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };

    public SigninFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        anhXa(view, savedInstanceState, container);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;

    }

    private void anhXa(final View view, final Bundle savedInstanceState, final ViewGroup container) {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        edt_email = (EditText) view.findViewById(R.id.edt_siFrg_username);
        edt_pass = (EditText) view.findViewById(R.id.edt_siFrg_password);
        txtV_signup = (TextView) view.findViewById(R.id.txtV_signup);
//        btn_signin = (Button) view.findViewById(btn_siFrg_signin);
//        btn_exit = (Button) view.findViewById(R.id.btn_siFrg_exit);
//        butt_loginFB = (Button) view.findViewById(R.id.butt_siFrg_loginFB);
//        butt_loginGmail = (Button) view.findViewById(R.id.butt_siFrg_loginGmail);
//        txt_forgotPass = (TextView) view.findViewById(R.id.txt_siFrg_forgotPass);
//        fab_signup = (FloatingActionButton) view.findViewById(R.id.fab_signup);
//        fab_signup.setOnClickListener(this);
        txtV_signup.setOnClickListener(this);
        btn_signin.setOnClickListener(this);
//        btn_exit.setOnClickListener(this);
//        butt_loginGmail.setOnClickListener(this);
//        butt_loginFB.setOnClickListener(this);
//        txt_forgotPass.setOnClickListener(this);
    }

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public void onStart() {
        super.onStart();
        isConnected = MyService.returnIsConnected();
        if (!isConnected) {
            Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_SHORT).show();
        }
        mAuth.addAuthStateListener(mAuthStateListener);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver, mIntentFilter);
    }

    @Override
    public void onStop() {
        getContext().unregisterReceiver(broadcastReceiver);
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtV_signup:
                if (isConnected) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
                    intent.putExtra(getActivity().getResources().getString(R.string.fragment_CODE),
                            getActivity().getResources().getString(R.string.frg_signup_CODE));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("isConnected", isConnected);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_siFrg_signin:
                if (isConnected) {
                    doSignin(view);
                } else
                    Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();

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

    void doSignin(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (edt_email.getText().toString().equals("")) {
            Snackbar.make(view, getResources().getString(R.string.txt_noemail), Snackbar.LENGTH_SHORT).show();
        } else if (edt_pass.getText().toString().equals("")) {
            Snackbar.make(view, getResources().getString(R.string.txt_nopass), Snackbar.LENGTH_SHORT).show();
        } else if (!edt_email.getText().toString().equals("")
                && !edt_pass.getText().toString().equals("")) {
            if (!isValidEmailAddress(edt_email.getText().toString())) {
                Snackbar.make(view, getResources().getString(R.string.txt_notemail), Snackbar.LENGTH_SHORT).show();
            } else {
                mprogressDialog = ProgressDialog.show(getActivity(),
                        getResources().getString(R.string.txt_plzwait),
                        getResources().getString(R.string.txt_logging), true, false);
                mAuth.signInWithEmailAndPassword(edt_email.getText().toString(), edt_pass.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                mprogressDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithEmail:onComplete", task.getException());
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), "failed login with: " + edt_email.getText().toString(),
//                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    getActivity().finish();
                                }
                            }
                        });
            }
        }
    }
}
