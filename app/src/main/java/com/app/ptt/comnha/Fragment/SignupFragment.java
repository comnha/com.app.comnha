package com.app.ptt.comnha.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.Users;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
        DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private static final String LOG=SigninFragment.class.getSimpleName();
    EditText editText_ho, editText_ten, editText_tenlot, editText_username, editText_email,
            editText_password, editText_confirmPass, editText_birth;
    Button butt_signup, butt_exit;
    TextView txt_forgotPass;
    Users createNewAccount;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatePickerDialog dpd;
    Calendar now;
    int day, month, year;
    boolean isConnected=true;
    IntentFilter mIntentFilter;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG+".onReceive form Service","isConnected= "+ intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        now = Calendar.getInstance();
        anhXa(view);
        Firebase.setAndroidContext(getContext());
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        return view;
    }

    private void anhXa(View view) {
        editText_ho = (EditText) view.findViewById(R.id.editText_ho);
        editText_ten = (EditText) view.findViewById(R.id.editText_ten);
        editText_tenlot = (EditText) view.findViewById(R.id.editText_tenLot);
        editText_username = (EditText) view.findViewById(R.id.editText_username);
        editText_email = (EditText) view.findViewById(R.id.editText_email);
        editText_password = (EditText) view.findViewById(R.id.editText_password);
        editText_confirmPass = (EditText) view.findViewById(R.id.editText_confirmPass);
        editText_birth = (EditText) view.findViewById(R.id.editText_birth);
        butt_signup = (Button) view.findViewById(R.id.butt_signup);
        butt_exit = (Button) view.findViewById(R.id.butt_signup_exit);
        dpd = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH), now.get(Calendar.DATE));
        butt_signup.setOnClickListener(this);
        butt_exit.setOnClickListener(this);
        editText_birth.setOnClickListener(this);
        dpd.setOnDismissListener(this);
        dpd.setOnCancelListener(this);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
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
        isConnected= MyService.returnIsConnected();
        if(!isConnected){
            Toast.makeText(getContext(),"Offline mode",Toast.LENGTH_SHORT).show();
        }
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver,mIntentFilter);
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        year = month = day = -1;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (year > -1) {
            editText_birth.setText(day + "/" + month + "/" + year);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        this.day = dayOfMonth;
        this.month = monthOfYear+1;
        this.year = year;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.butt_signup:
                if (editText_ho.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getActivity().getResources().getString(R.string.txt_noho), Snackbar.LENGTH_SHORT).show();
                } else if (editText_tenlot.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getActivity().getResources().getString(R.string.txt_notenlot), Snackbar.LENGTH_SHORT).show();
                } else if (editText_ten.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getActivity().getResources().getString(R.string.txt_noten), Snackbar.LENGTH_SHORT).show();
                } else if (editText_birth.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getActivity().getResources().getString(R.string.txt_nongsinh), Snackbar.LENGTH_SHORT).show();
                } else if (editText_username.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getActivity().getResources().getString(R.string.txt_noun), Snackbar.LENGTH_SHORT).show();
                } else if (editText_email.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getActivity().getResources().getString(R.string.txt_noemail), Snackbar.LENGTH_SHORT).show();
                } else if (editText_password.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getActivity().getResources().getString(R.string.txt_nopass), Snackbar.LENGTH_SHORT).show();
                } else if (editText_confirmPass.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getActivity().getResources().getString(R.string.txt_noconfirmpass), Snackbar.LENGTH_SHORT).show();
                } else {
                    if (!isValidEmailAddress(editText_email.getText().toString())) {
                        Snackbar.make(view, getActivity().getResources().getString(R.string.txt_notemail), Snackbar.LENGTH_SHORT).show();
                    } else {
                        if(isConnected) {
                            createNewAccount = new Users(getActivity().getApplicationContext());
                            createNewAccount.setHo(editText_ho.getText().toString());
                            createNewAccount.setTen(editText_ten.getText().toString());
                            createNewAccount.setTenlot(editText_tenlot.getText().toString());
                            createNewAccount.setUsername(editText_username.getText().toString());
                            createNewAccount.setEmail(editText_email.getText().toString());
                            createNewAccount.setPassword(editText_password.getText().toString());
                            createNewAccount.setConfirmPass(editText_confirmPass.getText().toString());
                            createNewAccount.setBirth(editText_birth.getText().toString());
                            createNewAccount.createNew();
                        }else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.butt_signup_exit:
                getActivity().onBackPressed();
                break;
            case R.id.editText_birth:
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
        }
    }
}
