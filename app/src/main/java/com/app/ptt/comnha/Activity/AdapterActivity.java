package com.app.ptt.comnha.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.ptt.comnha.Fragment.AddFoodFragment;
import com.app.ptt.comnha.Fragment.AddstoreFragment;
import com.app.ptt.comnha.Fragment.FooddetailFragment;
import com.app.ptt.comnha.Fragment.MapFragment;
import com.app.ptt.comnha.Fragment.NotificationFragment;
import com.app.ptt.comnha.Fragment.SigninFragment;
import com.app.ptt.comnha.Fragment.SignupFragment;
import com.app.ptt.comnha.Fragment.WritepostFragment;
import com.app.ptt.comnha.R;

public class AdapterActivity extends AppCompatActivity {
    static final String STATE_ADDPOST_FRAGMENT = "addpostFragment";
    static final int CHECK_ADDPOST_FRAGMENT = 1;
    String FRAGMENT_CODE = null;
    String fromFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter);
        Intent intent = getIntent();
        FRAGMENT_CODE = intent.getExtras().getString(getResources().getString(R.string.fragment_CODE));
        fromFrag = intent.getExtras().getString(getString(R.string.fromFrag));
        if (FRAGMENT_CODE.equals(getString(R.string.frag_writepost_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    WritepostFragment writepostFragment = new WritepostFragment();
                    writepostFragment.setArguments(getIntent().getExtras());

                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, writepostFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_storelist_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_addstore_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    AddstoreFragment addstoreFragment = new AddstoreFragment();
                    addstoreFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, addstoreFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_choosestore_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
//                    ChooselocaFragment chooselocaFragment = new ChooselocaFragment();
//                    chooselocaFragment.setArguments(getIntent().getExtras());
//                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, chooselocaFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_postdetail_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
//                    ViewpostFragment viewpostFragment = new ViewpostFragment();
//                    viewpostFragment.setArguments(getIntent().getExtras());
//                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, viewpostFragment)
//                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signin_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    SigninFragment signinFragment = new SigninFragment();
                    signinFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, signinFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signup_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    SignupFragment signupFragment = new SignupFragment();
                    signupFragment.setArguments(getIntent().getExtras());
                    signupFragment.setEmail(intent.getExtras().getString("email"));
                    signupFragment.setPass(intent.getExtras().getString("pass"));
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, signupFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_foodetail_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    FooddetailFragment fooddetailFragment = new FooddetailFragment();
                    fooddetailFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter,
                            fooddetailFragment).commit();
                }
            }

        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_themmon_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    AddFoodFragment addFoodFragment = new AddFoodFragment();
                    addFoodFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, addFoodFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_viewalbum_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
//                    ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
//                    viewAlbumFragment.setFromFrag(fromFrag);
//                    viewAlbumFragment.setArguments(getIntent().getExtras());
//                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, viewAlbumFragment)
//                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_notification_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    NotificationFragment notificationFragment = new NotificationFragment();
                    notificationFragment.setArguments(getIntent().getExtras());

                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, notificationFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_viewfood_CODE))) {
            Log.i("ZOOOOOOOOOOOO", "VIEW FOOD");
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    FooddetailFragment fooddetailFragment = new FooddetailFragment();
                    fooddetailFragment.setArguments(getIntent().getExtras());

                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, fooddetailFragment).commit();
                }
            }
        }else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_map_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    MapFragment mapFragment = new MapFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString(getResources().getString(R.string.frag_map_CODE),getResources().getString(R.string.frag_map_CODE));
                    mapFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, mapFragment).commit();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ADDPOST_FRAGMENT, CHECK_ADDPOST_FRAGMENT);
        Log.i("saveState", "saved");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}