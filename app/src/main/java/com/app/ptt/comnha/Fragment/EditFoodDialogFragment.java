package com.app.ptt.comnha.Fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseFood;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFoodDialogFragment extends DialogFragment {
    ProgressDialog mProgressDialog;
    EditText edtTenMon, edtGia;
    RatingBar rbDanhGia;
    Map<String, Object> childUpdates;
    Map<String, Object> foodValue;
    Button btnOK, btnCancel;
    DatabaseReference dbRef;
    Food editFood;

    public EditFoodDialogFragment() {
        // Required empty public constructor
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_food_dialog, container, false);
        editFood = ChooseFood.getInstance().getFood();
        childUpdates = new HashMap<>();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
//        stRef = FirebaseStorage.getInstance()
//                .getReferenceFromUrl(Const.STORAGE_PATH);
        edtTenMon = (EditText) view.findViewById(R.id.edt_tenmon);
//        edtTenMon.setText(ChooseFood.getInstance().getFood().getName());
//        edtGia=(EditText) view.findViewById(R.id.edt_gia);
//        edtGia.setText(ChooseFood.getInstance().getFood().getPrice()+"");
//        rbDanhGia=(RatingBar) view.findViewById(R.id.rb_danhGiaMon);
//        rbDanhGia.setRating(ChooseFood.getInstance().getFood().getRating());
        btnOK = (Button) view.findViewById(R.id.btn_OK);
        mProgressDialog = ProgressDialog.show(getActivity(),
                getResources().getString(R.string.txt_plzwait),
                "Đang xử lý",
                true, false);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Thông báo");
                alert.setMessage("Bạn có muốn sửa món ăn này không?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        editFood.setName(edtTenMon.getText().toString());
//                        editFood.setPrice(Long.parseLong(edtGia.getText().toString()));
                        foodValue = editFood.toMap();
                        // dialog.dismiss();
                        mProgressDialog.show();
//                        UserNotification notification = new UserNotification();
//                                String key1 = dbRef.child(getResources().getString(R.string.notification_CODE) + "admin").push().getKey();
//                                notification.setAccount(MyService.getUserAccount());
//                                notification.setDate(new Times().getDate());
//                                notification.setTime(new Times().getTime());
//                                notification.setType(11);
//                                notification.setFood(editFood);
//                                notification.setStore(ChooseFood.getInstance().getStore());
//                                notification.setReaded(false);
//                                notification.setTo("admin");
//                                Map<String, Object> notificationValue = notification.toMap();
//                                childUpdates = new HashMap<>();
//                                childUpdates.put(getResources().getString(R.string.notification_CODE) + "admin/" + key1, notificationValue);
//                                Map<String,Object> newFood=editFood.toMap();
////                        childUpdates.put(getResources().getString(R.string.thucdon_CODE)+editFood.getFoodID(),newFood);
//                                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        plzw8Dialog.dismiss();
//                                        Toast.makeText(getContext(), "Đã sửa thành công", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        plzw8Dialog.dismiss();
//                                        Toast.makeText(getContext(), "Lỗi khi sửa", Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
                    }
                });
                alert.setNegativeButton("Trờ về", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
