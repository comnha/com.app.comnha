package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.app.ptt.comnha.Modules.Times;
import com.app.ptt.comnha.FireBase.Notification;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.EditLocal;
import com.app.ptt.comnha.SingletonClasses.EditPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReasonStoreDialogFragment extends DialogFragment {
    ProgressDialog mProgressDialog;
    Button  btn_cancel, btn_ok;
    RadioGroup rg;
    RadioButton rbNd,rbKhac;
    boolean type=false;
    EditText edt_giaithich;
    int opt;
    private DatabaseReference dbRef;
    Map<String, Object> childUpdates;
    public ReasonStoreDialogFragment() {
        // Required empty public constructor
    }
    public void setType(int type){
        opt=type;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reason_store_dialog, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.txt_plzwait));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        rg=(RadioGroup) view.findViewById(R.id.rb_lydo);
        edt_giaithich=(EditText) view.findViewById(R.id.edt_giaithich);
        btn_cancel = (Button) view.findViewById(R.id.frg_reasonstore_btncancel);
        btn_ok = (Button) view.findViewById(R.id.frg_reasonstore_btnok);
        rbNd=(RadioButton) view.findViewById(R.id.rb_nd);
        rbKhac=(RadioButton)view.findViewById(R.id.rb_khac);
        edt_giaithich.setVisibility(View.INVISIBLE);
        if(opt==3) {
            rbNd.setVisibility(View.INVISIBLE);
            rbKhac.setVisibility(View.INVISIBLE);
            edt_giaithich.setVisibility(View.VISIBLE);
            rg.setVisibility(View.INVISIBLE);
        }
        else {
            rbNd.setVisibility(View.VISIBLE);
            rbKhac.setVisibility(View.VISIBLE);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.rb_nd:
                            type=false;
                            break;
                        case R.id.rb_khac:
                            type=true;
                            edt_giaithich.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });

        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(type && edt_giaithich.getText().equals("")&&opt!=3){
                   Toast.makeText(getContext(), "Vui lòng điền lý do.", Toast.LENGTH_SHORT).show();
               }else {
                   mProgressDialog.show();
                   if (opt == 1) {
                       Notification notification = new Notification();
                       String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + EditLocal.getInstance().getMyLocation().getUserId()).push().getKey();
                       notification.setAccount(MyService.getUserAccount());
                       notification.setDate(new Times().getDate());
                       notification.setTime(new Times().getTime());
                       notification.setLocation(EditLocal.getInstance().getMyLocation());
                       notification.setType(4);
                       if (type) {
                           notification.setReason(edt_giaithich.getText().toString());
                       } else {
                           notification.setReason("Nội dung không chính xác");
                       }
                       notification.setReaded(false);
                       childUpdates = new HashMap<String, Object>();
                       Map<String, Object> notificationValue = notification.toMap();
                       childUpdates.put(getResources().getString(R.string.notification_CODE) + EditLocal.getInstance().getMyLocation().getUserId() + "/" + key1, notificationValue);
                       dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isComplete()) {
                                   Toast.makeText(getContext(), "Đã gửi yêu cầu", Toast.LENGTH_SHORT).show();
                                   mProgressDialog.dismiss();
                                   getActivity().finish();
                               } else {
                                   mProgressDialog.dismiss();
                                   Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                   } else if (opt == 2) {
                       Notification notification = new Notification();
                       String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + "admin").push().getKey();
                       notification.setAccount(MyService.getUserAccount());
                       notification.setDate(new Times().getDate());
                       notification.setTime(new Times().getTime());
                       notification.setLocation(EditLocal.getInstance().getMyLocation());
                       notification.setType(10);
                       if (type) {
                           notification.setReason(edt_giaithich.getText().toString());
                       } else {
                           notification.setReason("Nội dung không chính xác");
                       }
                       notification.setReaded(false);
                       childUpdates = new HashMap<String, Object>();
                       Map<String, Object> notificationValue = notification.toMap();
                       childUpdates.put(getResources().getString(R.string.notification_CODE) + "admin" + "/" + key1, notificationValue);
                       Post post= EditPost.getInstance().getPost();
                       post.setReported(true);
                       post.setReporter(MyService.getUserAccount().getId());
                       Map<String, Object> postmap = post.toMap();
                       childUpdates.put(getResources().getString(R.string.posts_CODE) + post.getPostID(), postmap);
                       //  childUpdates.put(getResources().getString(R.string.notification_CODE)+EditLocal.getInstance().getMyLocation().getUserId()+ "/" + key1, notificationValue);
                       dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isComplete()) {
                                   Toast.makeText(getContext(), "Đã gửi yêu cầu", Toast.LENGTH_SHORT).show();
                                   mProgressDialog.dismiss();
                                   getActivity().finish();
                               } else {
                                   mProgressDialog.dismiss();
                                   Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                   } else if (opt == 3) {
                       Notification notification = new Notification();
                       String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + "admin").push().getKey();
                       notification.setAccount(MyService.getUserAccount());
                       notification.setDate(new Times().getDate());
                       notification.setTime(new Times().getTime());
                       notification.setLocation(EditLocal.getInstance().getMyLocation());
                       notification.setType(6);
                       notification.setReason(edt_giaithich.getText().toString());
                       notification.setReaded(false);
                       childUpdates = new HashMap<String, Object>();
                       Map<String, Object> notificationValue = notification.toMap();
                       childUpdates.put(getResources().getString(R.string.notification_CODE) + "admin" + "/" + key1, notificationValue);
                       dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isComplete()) {
                                   Toast.makeText(getContext(), "Đã yêu cầu.", Toast.LENGTH_SHORT).show();
                                   mProgressDialog.dismiss();
                                   getActivity().finish();
                               } else {
                                   mProgressDialog.dismiss();
                                   getActivity().finish();
                                   Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       });

                   }
               }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.txt_reportStore));
    }

    @Override
    public void onStart() {
        super.onStart();

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

}
