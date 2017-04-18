package com.app.ptt.comnha.Fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.app.ptt.comnha.Utils.AppUtils.isEqualsNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends BaseFragment implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
        DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private EditText editText_ho, editText_ten, editText_tenlot, editText_username, editText_email,
            editText_password, editText_confirmPass, editText_birth;
    private Button butt_signup;

    private DatePickerDialog dpd;
    private Calendar now;
    private int day, month, year;
    private String childDirection;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        now = Calendar.getInstance();
        anhXa(view);


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
        dpd = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH), now.get(Calendar.DATE));
        butt_signup.setOnClickListener(this);
        editText_birth.setOnClickListener(this);
        dpd.setOnDismissListener(this);
        dpd.setOnCancelListener(this);
       AppUtils.showKeyboard(getContext());
        editText_username.requestFocus();


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
                    AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_nointernet));
                break;
            case R.id.editText_birth:
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
        }

    }

    public void validateInput(View view) {
        if (isEqualsNull(editText_username)) {
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_noun));
            editText_username.requestFocus();
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
        if(!passWordLenght(AppUtils.getText(editText_password))){
            AppUtils.showSnackbarWithoutButton(view, getString(R.string.txt_passtooshort));
            editText_password.requestFocus();
            return;
        }
        if(!AppUtils.getText(editText_password).equals(AppUtils.getText(editText_confirmPass))){
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




        addUserInfo();
    }


    public void addUser() {
        auth.createUserWithEmailAndPassword(AppUtils.getText(editText_email), AppUtils.getText(editText_password))
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()) {
                            closeDialog();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(Const.INTENT_KEY_EMAIL, AppUtils.getText(editText_email));
                            returnIntent.putExtra(Const.INTENT_KEY_PASSWORD, AppUtils.getText(editText_password));
                            getActivity().setResult(Activity.RESULT_OK, returnIntent);
                            getActivity().finish();
                        } else {
                            removeUserInfo();
                        }

                    }
                });
    }

    public void addUserInfo() {
        showProgressDialog(getContext(), getString(R.string.text_signup), getString(R.string.txt_signup_loading));
        handleProgressDialog(getString(R.string.txt_tryagain));
        User user = new User();
        user.setEmail(AppUtils.getText(editText_email));
        user.setBirth(AppUtils.getText(editText_birth));
        user.setHo(AppUtils.getText(editText_ho));
        user.setTenlot(AppUtils.getText(editText_tenlot));
        user.setTen(AppUtils.getText(editText_ten));
        String key = dbRef.child(getString(R.string.users_CODE)).push().getKey();
        user.setUserID(key);
        Map<String, Object> userInfoMap = user.toMap();
        Map<String, Object> child = new HashMap<>();
        child.put(getResources().getString(R.string.users_CODE) + key, userInfoMap);
        childDirection = getResources().getString(R.string.users_CODE) + key;
        dbRef.updateChildren(child).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    addUser();
                } else {
                    closeDialog();
                    AppUtils.showSnackbarWithoutButton(getView(), getString(R.string.txt_tryagain));
                }
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



}
