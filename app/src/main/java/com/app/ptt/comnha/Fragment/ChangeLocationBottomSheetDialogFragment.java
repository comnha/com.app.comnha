package com.app.ptt.comnha.Fragment;


import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.R;

import java.util.Dictionary;
import java.util.Hashtable;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeLocationBottomSheetDialogFragment extends BottomSheetDialogFragment {
    ListView listVPro, listVDis;
    TextView txt_myloca;
    ArrayAdapter<CharSequence> mAdapterPro, mAdapterDis;
    Toolbar mToolbar;
    private Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<Integer, Integer>();
    OnChangeLocationListenner onChangeLocationListenner;
    String tinh = "";

    public interface OnChangeLocationListenner {
        void onChangeLocation(String Province, String District);

        void onChangetoMylocation(boolean isMylocation);
    }

    public void setOnChangeLocationListenner(OnChangeLocationListenner listener) {
        onChangeLocationListenner = listener;
    }

    private int getScroll() {
        View c = listVPro.getChildAt(0);
        int scrollY = -c.getTop();
        listViewItemHeights.put(listVPro.getFirstVisiblePosition(), c.getHeight());
        for (int i = 0; i < listVPro.getFirstVisiblePosition(); ++i) {
            if (listViewItemHeights.get(i) != null)
                scrollY += listViewItemHeights.get(i);
        }
        return scrollY;
    }

    public ChangeLocationBottomSheetDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_change_location_bottom_sheet_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();
        anhxa(contentView);
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
        }

    }

    private void anhxa(View contentView) {
        txt_myloca = (TextView) contentView.findViewById(R.id.frg_changeloca_txtMylocal);
        mToolbar = (Toolbar) contentView.findViewById(R.id.frg_changeloca_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        mToolbar.setTitle(getString(R.string.text_pickProvince));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        listVPro = (ListView) contentView.findViewById(R.id.frg_changeloca_listVPro);
        listVDis = (ListView) contentView.findViewById(R.id.frg_changeloca_listVDis);
        mAdapterPro = ArrayAdapter.createFromResource(getContext(),
                R.array.tinhtp, android.R.layout.simple_list_item_1);
        listVPro.setAdapter(mAdapterPro);
        listVPro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tinh = mAdapterPro.getItem(position).toString();
                switch (position) {
                    case 0:
                        mAdapterDis = ArrayAdapter.createFromResource(getContext(), R.array.hanoi, android.R.layout.simple_list_item_1);
                        listVDis.setAdapter(mAdapterDis);
                        break;
                    case 1:
                        mAdapterDis = ArrayAdapter.createFromResource(getContext(), R.array.hochiminh, android.R.layout.simple_list_item_1);
                        listVDis.setAdapter(mAdapterDis);
                        break;
                    case 2:
                        mAdapterDis = ArrayAdapter.createFromResource(getContext(), R.array.haiphong, android.R.layout.simple_list_item_1);
                        listVDis.setAdapter(mAdapterDis);
                        break;
                    case 3:
                        mAdapterDis = ArrayAdapter.createFromResource(getContext(), R.array.danang, android.R.layout.simple_list_item_1);
                        listVDis.setAdapter(mAdapterDis);
                        break;
                    case 4:
                        mAdapterDis = ArrayAdapter.createFromResource(getContext(), R.array.hagiang, android.R.layout.simple_list_item_1);
                        listVDis.setAdapter(mAdapterDis);
                        break;
                    case 38:
                        mAdapterDis = ArrayAdapter.createFromResource(getContext(), R.array.daklak, android.R.layout.simple_list_item_1);
                        listVDis.setAdapter(mAdapterDis);
                        break;
                    case 40:
                        mAdapterDis = ArrayAdapter.createFromResource(getContext(), R.array.lamdong, android.R.layout.simple_list_item_1);
                        listVDis.setAdapter(mAdapterDis);
                        break;
                    case 61:
                        mAdapterDis = ArrayAdapter.createFromResource(getContext(), R.array.daknong, android.R.layout.simple_list_item_1);
                        listVDis.setAdapter(mAdapterDis);
                        break;
                    case 36:
                        mAdapterDis = ArrayAdapter.createFromResource(getContext(), R.array.gialai, android.R.layout.simple_list_item_1);
                        listVDis.setAdapter(mAdapterDis);
                        break;
                    default:
                        mAdapterDis = null;
                        listVDis.setAdapter(mAdapterDis);
                }
                AnimationUtils.animatMoveListForward(listVPro, listVDis);
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                mToolbar.setTitle(getString(R.string.text_pickDistrict));
            }
        });
        listVDis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onChangeLocationListenner.onChangeLocation(tinh, mAdapterDis.getItem(position).toString());
                dismiss();
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listVDis.getTranslationX() != 0) {
                    dismiss();

                } else if (listVPro.getTranslationX() != 0) {
                    AnimationUtils.animatMoveListForback(listVPro, listVDis);
                    mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
                    mToolbar.setTitle(getString(R.string.text_pickProvince));
                }
            }
        });
        txt_myloca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeLocationListenner.onChangetoMylocation(true);
                dismiss();
            }
        });
    }

}
