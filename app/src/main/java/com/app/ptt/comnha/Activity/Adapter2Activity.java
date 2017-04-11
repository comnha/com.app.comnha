package com.app.ptt.comnha.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.ptt.comnha.Fragment.AddFoodFragment;
import com.app.ptt.comnha.Fragment.AddlocaFragment;
import com.app.ptt.comnha.Fragment.WritepostFragment;
import com.app.ptt.comnha.Fragment.FooddetailFragment;
import com.app.ptt.comnha.Fragment.NotificationFragment;
import com.app.ptt.comnha.Fragment.ProfiledetailFragment;
import com.app.ptt.comnha.Fragment.SigninFragment;
import com.app.ptt.comnha.Fragment.SignupFragment;
import com.app.ptt.comnha.Fragment.ViewpostFragment;
import com.app.ptt.comnha.R;

public class Adapter2Activity extends AppCompatActivity {
    static final String STATE_ADDPOST_FRAGMENT = "addpostFragment";
    static final int CHECK_ADDPOST_FRAGMENT = 1;
    String FRAGMENT_CODE = null;
    String fromFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter2);
        Intent intent = getIntent();
        FRAGMENT_CODE = intent.getExtras().getString(getResources().getString(R.string.fragment_CODE));
        fromFrag = intent.getExtras().getString(getString(R.string.fromFrag));
        if (FRAGMENT_CODE.equals(getString(R.string.frag_addpost_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    WritepostFragment writepostFragment = new WritepostFragment();
                    writepostFragment.setArguments(getIntent().getExtras());

                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, writepostFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_localist_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_addloca_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    AddlocaFragment addlocaFragment = new AddlocaFragment();
                    addlocaFragment.setArguments(getIntent().getExtras());

                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, addlocaFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_locadetail_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
//                    LocadetailFragment locadetailFragment = new LocadetailFragment();
//                    locadetailFragment.setArguments(getIntent().getExtras());
//                    Log.i("LocadetailFragment", "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCc");
//                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, locadetailFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_chooseloca_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
//                    ChooselocaFragment chooselocaFragment = new ChooselocaFragment();
//                    chooselocaFragment.setArguments(getIntent().getExtras());
//                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, chooselocaFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_chooseimg_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
//                    ChoosePhotoFragment choosePhotoFragment = new ChoosePhotoFragment();
//                    choosePhotoFragment.setArguments(getIntent().getExtras());
//                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, choosePhotoFragment).commit();
                }
            }

        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_viewpost_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    ViewpostFragment viewpostFragment = new ViewpostFragment();
                    viewpostFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, viewpostFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signin_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    SigninFragment signinFragment = new SigninFragment();
                    signinFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, signinFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signup_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    SignupFragment signupFragment = new SignupFragment();
                    signupFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, signupFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_vote_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
//                    DoVoteFragment dovoteFragment = new DoVoteFragment();
//                    dovoteFragment.setArguments(getIntent().getExtras());
//                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, dovoteFragment)
//                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_prodetail_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    ProfiledetailFragment proDetailFrag = new ProfiledetailFragment();
                    proDetailFrag.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, proDetailFrag)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_themmon_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    AddFoodFragment addFoodFragment = new AddFoodFragment();
                    addFoodFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, addFoodFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_viewalbum_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
//                    ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
//                    viewAlbumFragment.setFromFrag(fromFrag);
//                    viewAlbumFragment.setArguments(getIntent().getExtras());
//                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, viewAlbumFragment)
//                            .commit();
                }
            }
        }else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_notification_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    NotificationFragment notificationFragment=new NotificationFragment();
                    notificationFragment.setArguments(getIntent().getExtras());

                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2,notificationFragment).commit();
                }
            }
        } else if(FRAGMENT_CODE.equals(getResources().getString(R.string.frg_viewfood_CODE))){
            Log.i("ZOOOOOOOOOOOO","VIEW FOOD");
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    FooddetailFragment fooddetailFragment=new FooddetailFragment();
                    fooddetailFragment.setArguments(getIntent().getExtras());

                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2,fooddetailFragment).commit();
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
        if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_localist_CODE))) {
            finish();
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_locadetail_CODE))) {
            finish();
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_chooseloca_CODE))) {
            finish();
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_chooseimg_CODE))) {
            finish();
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_viewpost_CODE))) {
            finish();
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_vote_CODE))) {
            finish();
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_prodetail_CODE))) {
            finish();
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_viewalbum_CODE))) {
            finish();
        } else {
            new AlertDialog.Builder(this).setMessage("Bạn muốn hủy??")
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Trở lại", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}