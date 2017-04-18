package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.ptt.comnha.Models.FireBase.Account;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeProfileFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    Button btn_ok, btn_cancel;
    EditText editText_ho, editText_ten, editText_tenlot, editText_username, editText_email,
            editText_password, editText_confirmPass, editText_birth;
    Button butt_signup, butt_exit;
    FirebaseUser user;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatePickerDialog dpd;
    Calendar now;
    int day, month, year;
    ProgressDialog mProgressDialog;
    DatabaseReference dbRef;

    public ChangeProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_profile, container, false);
        now = Calendar.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        anhxa(view);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
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

    private void anhxa(View view) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.txt_plzwait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        editText_ho = (EditText) view.findViewById(R.id.frg_changeProfile_editText_ho);
        editText_ten = (EditText) view.findViewById(R.id.frg_changeProfile_editText_ten);
        editText_tenlot = (EditText) view.findViewById(R.id.frg_changeProfile_editText_tenLot);
        editText_username = (EditText) view.findViewById(R.id.frg_changeProfile_editText_username);
        editText_password = (EditText) view.findViewById(R.id.frg_changeProfile_editText_password);
        editText_confirmPass = (EditText) view.findViewById(R.id.frg_changeProfile_editText_confirmPass);
        editText_birth = (EditText) view.findViewById(R.id.frg_changeProfile_editText_birth);
        butt_signup = (Button) view.findViewById(R.id.frg_changeProfile_butt_signup);
        butt_exit = (Button) view.findViewById(R.id.frg_changeProfile_butt_signup_exit);
        dpd = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH), now.get(Calendar.DATE));
        butt_signup.setOnClickListener(this);
        butt_exit.setOnClickListener(this);
        editText_birth.setOnClickListener(this);
        dpd.setOnDismissListener(this);
        dpd.setOnCancelListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Thay đổi thông tin cá nhân");
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
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
        this.month = monthOfYear + 1;
        this.year = year;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_changeProfile_butt_signup:
                if (editText_ho.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), getString(R.string.txt_noho), Toast.LENGTH_SHORT).show();
                } else if (editText_tenlot.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), getString(R.string.txt_notenlot),
                            Toast.LENGTH_SHORT).show();
                } else if (editText_ten.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), getString(R.string.txt_noten),
                            Toast.LENGTH_SHORT).show();
                } else if (editText_birth.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), getString(R.string.txt_nongsinh),
                            Toast.LENGTH_SHORT).show();
                } else if (editText_username.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), getString(R.string.txt_noun),
                            Toast.LENGTH_SHORT).show();
                } else if (editText_confirmPass.getText().toString().trim()
                        .equals("")) {
                    Toast.makeText(getContext(), getString(R.string.txt_noconfirmpass), Toast.LENGTH_SHORT).show();
                } else if (editText_password.getText().toString().trim()
                        .equals("")) {
                    Toast.makeText(getContext(), getString(R.string.txt_nopass), Toast.LENGTH_SHORT).show();
                } else if (!LoginSession.getInstance().getPassword()
                        .equals(editText_password.getText().toString().trim())) {
                    Toast.makeText(getContext(), "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                } else if (!editText_confirmPass.getText().toString().trim()
                        .equals(editText_password.getText().toString().trim())) {
                    Toast.makeText(getContext(), "Mật khẩu xác nhận không đúng", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressDialog.show();
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(editText_username.getText().toString().trim())
                            .build();
                    user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isComplete()) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                String userID = LoginSession.getInstance().getUserID();
                                Account account = new Account();
                                account.setTen(editText_ten.getText().toString());
                                account.setHo(editText_ho.getText().toString());
                                account.setTenlot(editText_tenlot.getText().toString());
                                account.setBirth(editText_birth.getText().toString());
                                account.setPassword(LoginSession.getInstance().getPassword());
                                Map<String, Object> updateChild = new HashMap<String, Object>();
                                updateChild.put(getString(R.string.users_CODE)
                                        + userID, account);
                                dbRef.updateChildren(updateChild)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isComplete()) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(getContext(), task.getException().getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                } else
                                                    mProgressDialog.dismiss();
                                                dismiss();
                                            }
                                        });
                            }
                        }
                    });
                }
                break;
            case R.id.frg_changeProfile_butt_signup_exit:
                new AlertDialog.Builder(getContext())
                        .setMessage("Bạn muốn thoát")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        }).setNegativeButton("Trở lại", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.frg_changeProfile_editText_birth:
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        editText_ten.setText(LoginSession.getInstance().getTen());
        editText_ho.setText(LoginSession.getInstance().getHo());
        editText_tenlot.setText(LoginSession.getInstance().getTenlot());
        editText_birth.setText(LoginSession.getInstance().getNgaysinh());
        editText_username.setText(LoginSession.getInstance().getUsername());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }
}
