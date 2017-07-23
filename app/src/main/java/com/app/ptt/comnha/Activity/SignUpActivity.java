package com.app.ptt.comnha.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.app.ptt.comnha.Utils.AppUtils.isEqualsNull;
import static com.app.ptt.comnha.Utils.AppUtils.keyboard;
import static com.app.ptt.comnha.Utils.AppUtils.showSnackbar;

/**
 * Created by ciqaz on 22/07/2017.
 */

public class SignUpActivity extends BaseActivity implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
        DatePickerDialog.OnDateSetListener, View.OnClickListener  {
    private EditText editText_ho, editText_ten, editText_tenlot, editText_un, editText_email,
            editText_password, editText_confirmPass, editText_birth;
    private Button butt_signup;
    FirebaseAuth auth;
    private DatePickerDialog dpd;
    private Calendar now;
    private int day, month, year;
    private String childDirection;
    String email, pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signup);
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        now = Calendar.getInstance();
        ref();
        if(getIntent().getStringExtra("email")!=null&&getIntent().getStringExtra("pass")!=null){
            editText_email.setText(getIntent().getStringExtra("email"));
            editText_password.setText(getIntent().getStringExtra("pass"));
        }
    }
    private void ref() {
        editText_ho = (EditText)   findViewById(R.id.editText_ho_signup);
        editText_ten = (EditText)   findViewById(R.id.editText_ten_signup);
        editText_tenlot = (EditText)   findViewById(R.id.editText_tenLot_signup);
        editText_un = (EditText)   findViewById(R.id.editText_un_signup);
        editText_email = (EditText)   findViewById(R.id.editText_email_signup);
        editText_email.setText(email);
        editText_password = (EditText)   findViewById(R.id.editText_password_signup);
        editText_password.setText(pass);
        editText_confirmPass = (EditText)   findViewById(R.id.editText_confirmPass_signup);
        editText_birth = (EditText)   findViewById(R.id.editText_birth_signup);
        butt_signup = (Button)   findViewById(R.id.butt_signup);
        dpd = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH), now.get(Calendar.DATE));
        butt_signup.setOnClickListener(this);
        editText_birth.setOnClickListener(this);
        dpd.setOnDismissListener(this);
        dpd.setOnCancelListener(this);
        AppUtils.showKeyboard(this);
        editText_un.requestFocus();


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
                if (MyService.isNetworkAvailable(this)) {
                    validateInput(view);
                } else
                    showSnackbar(this, getWindow().getDecorView(), getString(R.string.text_not_internet), getString(R.string.text_connect), Const.SNACKBAR_GO_ONLINE, Snackbar.LENGTH_SHORT);
                break;
            case R.id.editText_birth_signup:
                dpd.show(getFragmentManager(), "Datepickerdialog");
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
        if (!AppUtils.isValidEmailAddress(AppUtils.getText(editText_email))) {
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
        if (!AppUtils. passWordLenght(AppUtils.getText(editText_password))) {
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
        keyboard(false,this);

    }


    public void addUser() {
        showProgressDialog( getString(R.string.text_signup), getString(R.string.txt_signup_loading));
        handleProgressDialog();
        auth.createUserWithEmailAndPassword(AppUtils.getText(editText_email),
                AppUtils.getText(editText_password))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isComplete()) {
                                addUserInfo(task.getResult().getUser().getUid());
                            } else {
                                AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Email đã tồn tại");
                                closeDialog();
                            }
                        }catch (Exception e){
                            AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Email đã tồn tại");
                            closeDialog();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), "Email đã tồn tại");
                closeDialog();
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
        user.setStatus(true);
//        String key = dbRef.child(getString(R.string.users_CODE)).push().getKey();
        Map<String, Object> userInfoMap = user.toMap();
        Map<String, Object> child = new HashMap<>();
        child.put(getResources().getString(R.string.users_CODE) + key, userInfoMap);
        childDirection = getResources().getString(R.string.users_CODE) + key;
        dbRef.updateChildren(child).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Intent intent=getIntent();
                    intent.putExtra(Const.INTENT_KEY_EMAIL,editText_email.getText().toString());
                    intent.putExtra(Const.INTENT_KEY_PASSWORD,editText_password.getText().toString());
                    setResult(RESULT_OK,intent);
                    finish();
                  //sign in fragment
                } else {
                    AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.txt_tryagain));
                }
                closeDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.txt_tryagain));
                closeDialog();
            }
        });
    }

    private void removeUserInfo() {
        dbRef.child(childDirection).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {

                    AppUtils.showSnackbarWithoutButton(getWindow().getDecorView(), getString(R.string.txt_tryagain));
                }
                closeDialog();
            }
        });
    }


}
