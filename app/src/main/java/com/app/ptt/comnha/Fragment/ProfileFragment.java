package com.app.ptt.comnha.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.FireBase.Account;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by PTT on 10/25/2016.
 */

public class ProfileFragment extends Fragment {
        DatabaseReference dbRef;
    ValueEventListener profileValueEventListener;
    TextView txt_un, txt_Hoten, txt_Ngsinh, txt_email;
    ImageView btn_changePro;
    ChangeProfileFragment changeProfileDialog;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        anhxa(view);
        profileValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Account account = dataSnapshot.getValue(Account.class);
                txt_Hoten.setText(account.getHo() + " " + account.getTenlot() + " " + account.getTen());
                txt_Ngsinh.setText(account.getBirth());
                LoginSession.getInstance().setTen(account.getTen());
                LoginSession.getInstance().setHo(account.getHo());
                LoginSession.getInstance().setTenlot(account.getTenlot());
                LoginSession.getInstance().setNgaysinh(account.getBirth());
                LoginSession.getInstance().setPassword(account.getPassword());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getResources().getString(R.string.users_CODE) +//liệt kê tất cả
                LoginSession.getInstance().getUserID()).addListenerForSingleValueEvent(profileValueEventListener);
        return view;
    }

    void anhxa(View view) {
        changeProfileDialog = new ChangeProfileFragment();
        changeProfileDialog.setCancelable(false);
        txt_un = (TextView) view.findViewById(R.id.frag_profile_txtUername);
        txt_Hoten = (TextView) view.findViewById(R.id.frag_profile_txtHoten);
        txt_Ngsinh = (TextView) view.findViewById(R.id.frag_profilel_txtNgsinh);
        txt_email = (TextView) view.findViewById(R.id.frag_profile_txtEmail);
        btn_changePro = (ImageView) view.findViewById(R.id.btn_changeProfile);
        btn_changePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfileDialog.show(getActivity().getSupportFragmentManager(),
                        "fragment_changProfile");
            }
        });
        txt_email.setText(LoginSession.getInstance().getEmail());
        txt_un.setText(LoginSession.getInstance().getUsername());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dbRef.removeEventListener(profileValueEventListener);
    }
}
