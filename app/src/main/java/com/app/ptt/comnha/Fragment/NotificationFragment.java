package com.app.ptt.comnha.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.FragmentPagerAdapter;
import com.app.ptt.comnha.Adapters.Notification_rcycler_adapter;
import com.app.ptt.comnha.FireBase.Notification;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements View.OnClickListener {
    private CollapsingToolbarLayout collapsLayout;
    Toolbar mToolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView profileImgV;
    DatabaseReference dbRef;
    ArrayList<Notification> listNoti;
    Map<String, Object> childUpdates;
    ChildEventListener notificatonChildEventListener;
    FragmentPagerAdapter fragmentPagerAdapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        anhxa(view);
        setHasOptionsMenu(true);

        return view;
    }

    void anhxa(View view) {
        listNoti=new ArrayList<>();
        mToolbar = (Toolbar) view.findViewById(R.id.frag_noti_toolbar);
        viewPager = (ViewPager) view.findViewById(R.id.frag_noti_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.frag_noti_tablayout);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(MyService.getUserAccount().getUsername() + "'s notification");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setShowHideAnimationEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        collapsLayout = (CollapsingToolbarLayout) view.findViewById(R.id.frag_noti_collapsing_toolbar);
        collapsLayout.setTitle(MyService.getUserAccount().getUsername());
        fragmentPagerAdapter = new FragmentPagerAdapter(
                activity.getSupportFragmentManager(), activity.getApplicationContext(),2);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        profileImgV = (ImageView) view.findViewById(R.id.frag_noti_imgV_profile);
        profileImgV.setOnClickListener(this);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        ChooseLoca.getInstance().setLocation(null);

    }

    @Override
    public void onClick(View v) {
    }
}
