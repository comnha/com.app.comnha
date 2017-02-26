package com.app.ptt.comnha.Fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Photolist_rcyler_adapter;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.DoPost;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChoosePhotoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static int MEDIASTORE_LOADED_ID = 0;
    RecyclerView mRecyclerView;
    Photolist_rcyler_adapter mAdapter;
    Toolbar toolBar;
    public ChoosePhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choosephoto, container, false);
        getLoaderManager().initLoader(MEDIASTORE_LOADED_ID, null, this);
        if (savedInstanceState != null) {
            Log.i("notNullState", "notnUll");
        } else {
            anhxa(view);
        }
        setHasOptionsMenu(true);
        return view;
    }

    private void anhxa(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_choosept_recyclerV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new Photolist_rcyler_adapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        toolBar = (Toolbar) view.findViewById(R.id.frg_choosept_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolBar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.text_upImage));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.action_chosept:
                try {
                    Log.i("onOptionsItemSelected",mAdapter.getFiles().size()+"- SIZEEEEEEEEEEEEEE");
                    if (mAdapter.getFiles().size() > 0) {
                        Log.i("onOptionsItemSelected",mAdapter.getFiles().get(0)+"");
                        DoPost.getInstance().setFiles(mAdapter.getFiles());
                        MyService.setStatusChooseImg(true);
                        getActivity().finish();
                    }
                } catch (NullPointerException mess) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.txt_nochosephoto), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu_chosept, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE
        };
        String selection = MediaStore.Images.Media.MIME_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        return new CursorLoader(
                getActivity().getApplicationContext(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Images.Media.MIME_TYPE + "=" + "\'image/jpeg\'", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

}
