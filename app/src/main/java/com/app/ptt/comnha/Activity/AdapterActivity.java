package com.app.ptt.comnha.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.ptt.comnha.Dialog.AddFoodDialog;
import com.app.ptt.comnha.Fragment.AddstoreFragment;
import com.app.ptt.comnha.Fragment.MapFragment;
import com.app.ptt.comnha.Fragment.WritepostFragment;
import com.app.ptt.comnha.Models.FireBase.Store;
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
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_addstore_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    AddstoreFragment addstoreFragment = new AddstoreFragment();
                    addstoreFragment.setArguments(getIntent().getExtras());
                    if(getIntent().getSerializableExtra("STORE")!=null){
                       addstoreFragment.setEditStore((Store)getIntent().getSerializableExtra("STORE"));
                    }
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, addstoreFragment).commit();
                }
            }
        }  else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_themmon_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    AddFoodDialog addFoodDialog = new AddFoodDialog();
                    addFoodDialog.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, addFoodDialog)
                            .commit();
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