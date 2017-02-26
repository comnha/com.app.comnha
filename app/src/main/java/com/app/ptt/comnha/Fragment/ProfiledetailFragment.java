package com.app.ptt.comnha.Fragment;


import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.app.ptt.comnha.Adapters.FragmentPagerAdapter;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.LoginSession;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfiledetailFragment extends Fragment implements View.OnClickListener {
    private CollapsingToolbarLayout collapsLayout;
    Toolbar mToolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView profileImgV;
    FragmentPagerAdapter fragmentPagerAdapter;

    public ProfiledetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profiledetail, container, false);
        anhxa(view);
        setHasOptionsMenu(true);
        return view;
    }

    void anhxa(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.frag_prodetail_toolbar);
        viewPager = (ViewPager) view.findViewById(R.id.frag_prodetail_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.frag_prodetail_tablayout);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(LoginSession.getInstance().getUsername().trim() + "'s profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setShowHideAnimationEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        collapsLayout = (CollapsingToolbarLayout) view.findViewById(R.id.frag_prodetail_collapsing_toolbar);
        collapsLayout.setTitle(LoginSession.getInstance().getUsername());
        fragmentPagerAdapter = new FragmentPagerAdapter(
                activity.getSupportFragmentManager(), activity.getApplicationContext(),1);
        viewPager.setAdapter(fragmentPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        profileImgV = (ImageView) view.findViewById(R.id.frag_prodetail_imgV_profile);
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
    public void onDetach() {
        super.onDetach();
        ChooseLoca.getInstance().setLocation(null);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_prodetail_imgV_profile:
                PopupMenu popupMenu = new PopupMenu(getActivity(), profileImgV, Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_upavatar, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_chosefromloca:
                                break;
                            case R.id.action_chosefromUploaded:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;
        }
    }
}
