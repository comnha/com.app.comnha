package com.app.ptt.comnha.Fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.app.ptt.comnha.Utils.AppUtils.isEqualsNull;
import static com.app.ptt.comnha.Utils.AppUtils.showSnackbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends BaseFragment implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
        DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private EditText editText_ho, editText_ten, editText_tenlot, editText_un, editText_email,
            editText_password, editText_confirmPass, editText_birth;
    private Button butt_signup;

    private DatePickerDialog dpd;
    private Calendar now;
    private int day, month, year;
    private String childDirection;
    String email, pass;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        now = Calendar.getInstance();
        ref(view);
        return view;
    }

    private void ref(View view) {
        editText_ho = (EditText) view.findViewById(R.id.editText_ho_signup);
        editText_ten = (EditText) view.findViewById(R.id.editText_ten_signup);
        editText_tenlot = (EditText) view.findViewById(R.id.editText_tenLot_signup);
        editText_un = (EditText) view.findViewById(R.id.editText_un_signup);
        editText_email = (EditText) view.findViewById(R.id.editText_email_signup);
        editText_email.setText(email);
        editText_password = (EditText) view.findViewById(R.id.editText_password_signup);
        editText_password.setText(pass);
        editText_confirmPass = (EditText) view.findViewById(R.id.editText_confirmPass_signup);
        editText_birth = (EditText) view.findViewById(R.id.editText_birth_signup);
        butt_signup = (Button) view.findViewById(R.id.butt_signup);
        dpd = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH), now.get(Calendar.DATE));
        butt_signup.setOnClickListener(this);
        editText_birth.setOnClickListener(this);
        dpd.setOnDismissListener(this);
        dpd.setOnCancelListener(this);
        AppUtils.showKeyboard(getContext());
        editText_un.requestFocus();


    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }


    @Override
    public void onStart() {
        super.onStart();


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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.butt_signup:
                if (isNetworkConnected) {
                    validateInput(view);
                } else
                    showSnackbar(getActivity(), getView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_SHORT);
                break;
            case R.id.editText_birth_signup:
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
        }

    }

    public void validateInput(View view) {
        if (isEqualsNull(editText_un)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_noun));
            editText_un.requestFocus();
            return;
        }
        if (isEqualsNull(editText_email)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_noemail));
            editText_email.requestFocus();
            return;
        }
        if (!isValidEmailAddress(AppUtils.getText(editText_email))) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_notemail));
            editText_email.requestFocus();
            return;
        }
        if (isEqualsNull(editText_password)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_nopass));
            editText_password.requestFocus();
            return;
        }
        if (isEqualsNull(editText_confirmPass)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_noconfirmpass));
            editText_confirmPass.requestFocus();
            return;
        }
        if (!passWordLenght(AppUtils.getText(editText_password))) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_passtooshort));
            editText_password.requestFocus();
            return;
        }
        if (!AppUtils.getText(editText_password).equals(AppUtils.getText(editText_confirmPass))) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_notmatchpassword));
            editText_confirmPass.requestFocus();
            return;
        }

        if (isEqualsNull(editText_ho)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_noho));
            editText_ho.requestFocus();
            return;
        }
        if (isEqualsNull(editText_tenlot)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_notenlot));
            editText_tenlot.requestFocus();
            return;
        }
        if (isEqualsNull(editText_ten)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_noten));
            editText_ten.requestFocus();
            return;
        }
        if (isEqualsNull(editText_birth)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_nongsinh));
            editText_birth.requestFocus();
            return;
        }

        addUser();
//        addUserInfo();
    }


    public void addUser() {
        showProgressDialog(getContext(), getString(R.string.text_signup), getString(R.string.txt_signup_loading));
        handleProgressDialog(getString(R.string.txt_tryagain));
        auth.createUserWithEmailAndPassword(AppUtils.getText(editText_email),
                AppUtils.getText(editText_password))
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isComplete()) {
                                addUserInfo(task.getResult().getUser().getUid());
                            } else {
                                AppUtils.showSnackbarWithoutButton(getView(), "Email đã tồn tại");
                                closeDialog();
                            }
                        }catch (Exception e){
                            AppUtils.showSnackbarWithoutButton(getView(), "Email đã tồn tại");
                            closeDialog();
                        }

                    }
                });
    }

    public void addUserInfo(String key) {
        User user = new User(AppUtils.getText(editText_un),
                AppUtils.getText(editText_email),
                AppUtils.getText(editText_password),
                AppUtils.getText(editText_ho),
                AppUtils.getText(editText_ten),
                AppUtils.getText(editText_tenlot),
                AppUtils.getText(editText_birth));
//        String key = dbRef.child(getString(R.string.users_CODE)).push().getKey();
        Map<String, Object> userInfoMap = user.toMap();
        Map<String, Object> child = new HashMap<>();
        child.put(getResources().getString(R.string.users_CODE) + key, userInfoMap);
        childDirection = getResources().getString(R.string.users_CODE) + key;
        dbRef.updateChildren(child).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {

                    getActivity().finish();
                    SigninFragment signinFragment = new SigninFragment();
                    Bundle args = new Bundle();
                    args.putString("name", AppUtils.getText(editText_email));
                    args.putString("pass",AppUtils.getText(editText_password));
                    signinFragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, signinFragment)
                            .commit();

                } else {
                    AppUtils.showSnackbarWithoutButton(getView(), getString(R.string.txt_tryagain));
                }
                closeDialog();
            }
        });
    }

    private void removeUserInfo() {
        dbRef.child(childDirection).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {

                    AppUtils.showSnackbarWithoutButton(getView(), getString(R.string.txt_tryagain));
                }
                closeDialog();
            }
        });
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public void setPass(String pass) {
        this.pass = pass;
    }
}
