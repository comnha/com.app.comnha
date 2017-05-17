package com.app.ptt.comnha.Dialog;

import android.app.Dialog;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class BlockUserDialog extends DialogFragment {
    CircularImageView imgv_avatar;
    TextView txtv_un, txtv_email;
    StorageReference stRef;
    User user;
    OnPosNegListener onPosNegListener;
    Button btn_neg, btn_pos;

    public void setBlock(User user, Const.BLOCK_TYPE type) {
        this.user = user;
        switch (type) {
            case BLOCK_ADDSTORE:
                this.user.setAddstoreBlocked(true);
                break;
            case BLOCK_ADDFOOD:
                this.user.setAddfoodBlocked(true);
                break;
            case BLOCK_WRITEPOST:
                this.user.setWritepostBlocked(true);
                break;
            case BLOCK_REPRTFOOD:
                this.user.setReportfoodBlocked(true);
                break;
            case BLOCK_REPRTPOST:
                this.user.setReportpostBlocked(true);
                break;
            case BLOCK_REPRTSTORE:
                this.user.setReportstoreBlocked(true);
                break;
            case BLOCK_REPRTIMG:
                this.user.setReportimgBlocked(true);
                break;
        }
    }

    public interface OnPosNegListener {
        void onPositive(boolean isClicked, Map<String, Object> childUpdate, Dialog dialog);

        void onNegative(boolean isClicked, Dialog dialog);
    }

    public void setOnPosNegListener(OnPosNegListener
                                            onPosNegListener) {
        this.onPosNegListener = onPosNegListener;
    }

    public void setBlock(int block) {

    }

    public BlockUserDialog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_user_dialog, container,
                false);
        stRef = FirebaseStorage.getInstance().
                getReferenceFromUrl(getString(R.string.firebaseStorage_path));
        ref(view);
        if (user != null) {
            getdata();
        } else {
            getDialog().dismiss();
            Toast.makeText(getContext(), getString(R.string.txt_error),
                    Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void getdata() {
        if (!user.getAvatar().equals("")) {
            StorageReference imgRef = stRef.child(user.getAvatar());
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getContext())
                            .load(uri)
                            .into(imgv_avatar);
                }
            });
        } else {
            imgv_avatar.setImageResource(R.drawable.ic_account_circle_50black_48dp);
        }
        txtv_email.setText(getString(R.string.text_email) + ": " +
                user.getEmail());
        txtv_un.setText(getString(R.string.text_username) + ": "
                + user.getUn());
    }

    private void ref(View view) {
        imgv_avatar = (CircularImageView) view.findViewById(R.id.imgv_avatar_blockuser);
        txtv_un = (TextView) view.findViewById(R.id.txtv_un_blockuser);
        txtv_email = (TextView) view.findViewById(R.id.txtv_email_blockuser);
        btn_neg = (Button) view.findViewById(R.id.btn_negative_blockuser);
        btn_pos = (Button) view.findViewById(R.id.btn_positive_blockuser);
        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPosNegListener.onNegative(true, getDialog());
            }
        });
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = user.getuID();
//                Toast.makeText(getContext(), key, Toast.LENGTH_LONG).show();
                User childUser = user;
                childUser.setuID(null);
                Map<String, Object> userValue = childUser.toMap();
                Map<String, Object> childUpdate = new HashMap<>();
                childUpdate.put(getString(R.string.users_CODE)
                        + key, userValue);
                onPosNegListener.onPositive(true, childUpdate, getDialog());
            }
        });
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.text_blockuser));
        return dialog;
    }
}
