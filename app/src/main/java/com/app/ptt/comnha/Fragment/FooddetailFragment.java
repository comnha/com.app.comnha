package com.app.ptt.comnha.Fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Activity.PostdetailActivity;
import com.app.ptt.comnha.Adapters.Post_recycler_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Dialog.AddFoodDialog;
import com.app.ptt.comnha.Dialog.ReportDialog;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseFood;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.ChooseStore;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.Utils.AppUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.ptt.comnha.Const.Const.REPORTS.REPORT_FOOD;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class FooddetailFragment extends Fragment {
    private static final String LOG = FooddetailFragment.class.getSimpleName();
    DatabaseReference dbRef;
    MenuInflater inflater;
    StorageReference stRef;
    ArrayList<Food> foodList;
    RecyclerView postRecyclerView;
    Post_recycler_adapter postAdapter;
    RecyclerView.LayoutManager postLayoutManager;
    TextView txt_name, txt_price, txt_comment,txtUser;
    ImageView imgv_photo;
    RatingBar ratingBar;
    ArrayList<Post> postlist;
    ValueEventListener postEventListener;
    ActionBar actionBar;
    Toolbar toolbar;
    Food food;
    String storeID = "", foodID = "";
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    boolean isConnected = false;
    IntentFilter mIntentFilter;
    ProgressDialog plzw8Dialog;
    Menu pubMenu = null;

    public FooddetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        isConnected = MyService.isNetworkAvailable(getActivity());
        if (!isConnected) {
            Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fooddetail, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Const.DATABASE_PATH);
//        locaID = ChooseFood.getInstance().getStore().getLocaID();
        stRef = FirebaseStorage.getInstance().getReferenceFromUrl(Const.STORAGE_PATH);
        ref(view);
        if (ChooseFood.getInstance().getFood() != null) {
            foodID = ChooseFood.getInstance().getFood().getFoodID();
            getFood(foodID);
        } else {
            getActivity().onBackPressed();
        }
        return view;
    }

    private void getFood(String id) {

        ValueEventListener foodValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                food = dataSnapshot.getValue(Food.class);
                String key = dataSnapshot.getKey();
                food.setFoodID(key);
                storeID = food.getStoreID();
                getData();
                if(pubMenu!=null) {
                    List<Pair<Integer, String>> contents = returnContentMenuItems();
                    pubMenu = AppUtils.createMenu(pubMenu, contents);
                    loadMenu();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.food_CODE) + id)
                .addValueEventListener(foodValueListener);
    }

    public void loadMenu() {
        super.onCreateOptionsMenu(pubMenu, inflater);
    }
    private void getCustomUser( final String key){
        ValueEventListener userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User ownUser= dataSnapshot.getValue(User.class);
                ownUser.setuID(key);
                txtUser.setText(ownUser.getUn());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.child(getString(R.string.users_CODE)
                + key)
                .addListenerForSingleValueEvent(userValueListener);

    }
    public void getData() {

        txt_comment.setText(food.getComment());
        txt_name.setText(food.getName());
        getCustomUser(food.getUserID());
        txt_price.setText(food.getPrice() + "");
        ratingBar.setRating(food.getTotal() == 0 ? 0 : food.getRating() / food.getTotal());
        if (food.getImgBitmap() == null) {
            StorageReference imgRef = stRef.child(food.getFoodImg());
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getContext())
                            .load(uri)
                            .into(imgv_photo);
                    try {
                        Bitmap imgBitmap = ((BitmapDrawable) imgv_photo.getDrawable())
                                .getBitmap();
                        food.setImgBitmap(imgBitmap);
                    } catch (NullPointerException mess) {

                    }
                }
            });
        } else {
            imgv_photo.setImageBitmap(food.getImgBitmap());
            Bitmap imgBitmap = ((BitmapDrawable) imgv_photo.getDrawable())
                    .getBitmap();
            food.setImgBitmap(imgBitmap);
        }

        postEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    Post post = dataItem.getValue(Post.class);
                    post.setPostID(dataItem.getKey());
                    postlist.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbRef.child(
                getResources().getString(R.string.posts_CODE))
                .orderByChild("isHidden_foodID")
                .equalTo(false + "_" + foodID)
                .addListenerForSingleValueEvent(postEventListener);

    }

    private void ref(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources()
                    .getColor(R.color.color_notify_reportfood));
        }
        txt_name = (TextView) view.findViewById(R.id.txtv_name_fooddetail);
        txt_price = (TextView) view.findViewById(R.id.txtv_price_fooddetail);
        txt_comment = (TextView) view.findViewById(R.id.txtv_comment_fooddetail);
        imgv_photo = (ImageView) view.findViewById(R.id.imgv_photo_fooddetail);
        ratingBar = (RatingBar) view.findViewById(R.id.rb_rating_fooddetail);
        ratingBar.setIsIndicator(true);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_fooddetail);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        txtUser= (TextView) view.findViewById(R.id.txt_user);
        toolbar.setBackgroundColor(getResources()
                .getColor(R.color.color_notify_reportfood));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setTitle(getString(R.string.txt_fooddetail));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        postlist = new ArrayList<>();
        postRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_post_fooddetail);
        postRecyclerView.setHasFixedSize(true);
        foodList = new ArrayList<>();
        postLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        postRecyclerView.setLayoutManager(postLayoutManager);
        postAdapter = new Post_recycler_adapter(postlist, getActivity(), stRef);
        postRecyclerView.setAdapter(postAdapter);
        postAdapter.setOnItemClickLiestner(new Post_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Post post, View itemView) {
                Intent intent_postdetail = new Intent(getContext(),
                        PostdetailActivity.class);
                intent_postdetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat option_postbanner
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), itemView.findViewById(R.id.imgv_banner_postitem),
                        "postBanner");
                ChoosePost.getInstance().setPost(post);
                startActivity(intent_postdetail, option_postbanner.toBundle());
            }
        });
        plzw8Dialog = AppUtils.setupProgressDialog(getActivity(),
                getString(R.string.txt_plzwait), null, false, false,
                ProgressDialog.STYLE_SPINNER, 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu = AppUtils.createMenu(menu, returnContentMenuItems());
        pubMenu = menu;
        this.inflater=inflater;
        super.onCreateOptionsMenu(menu, inflater);

    }

    private List<Pair<Integer, String>> returnContentMenuItems() {
        int role = 0;
        String uID = "";

        if (LoginSession.getInstance().getUser() != null) {
            role = LoginSession.getInstance().getUser().getRole();
            uID = LoginSession.getInstance().getUser().getuID();
        }
        List<Pair<Integer, String>> contents = new ArrayList<>();
        if (role == 1) {
            contents.add(new Pair<Integer, String>
                    (R.string.txt_changeinfo, getString(R.string.txt_changeinfo)));
            contents.add(new Pair<Integer, String>
                    (R.string.txt_delfood, getString(R.string.txt_delfood)));
            if (food.isHidden()) {
                contents.add(new Pair<Integer, String>
                        (R.string.text_showfood, getString(R.string.text_showfood)));
            } else {
                contents.add(new Pair<Integer, String>
                        (R.string.text_hidefood, getString(R.string.text_hidefood)));
            }
        } else {
            contents.add(new Pair<Integer, String>
                    (R.string.txt_report, getString(R.string.txt_report)));
            if(food!=null) {
                if (uID.equals(food.getUserID())) {
                    if (food.isHidden()) {
                        contents.add(new Pair<Integer, String>
                                (R.string.text_hidefood, getString(R.string.text_showfood)));
                    } else {
                        contents.add(new Pair<Integer, String>
                                (R.string.text_hidefood, getString(R.string.text_hidefood)));
                    }
                }
            }
        }
        return contents;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.txt_report:
                ReportDialog reportDialog = new ReportDialog();
                reportDialog.setReport(REPORT_FOOD, food);
                reportDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddfoodDialog);
                reportDialog.setOnPosNegListener(new ReportDialog.OnPosNegListener() {
                    @Override
                    public void onPositive(boolean isClicked, Map<String,
                            Object> childUpdate, final Dialog dialog) {
                        if (isClicked) {
                            dialog.dismiss();
                            plzw8Dialog.show();
                            dbRef.updateChildren(childUpdate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            plzw8Dialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.show();
                                    plzw8Dialog.dismiss();
                                    Toast.makeText(getActivity(), e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onNegative(boolean isClicked, Dialog dialog) {
                        if (isClicked) {
                            dialog.dismiss();
                        }
                    }
                });
                reportDialog.show(getActivity().getSupportFragmentManager(), "report_food");
                return true;
            case R.string.txt_changeinfo:
                AddFoodDialog addFoodDialog = new AddFoodDialog();
                addFoodDialog.setEditFoood(true, food);
                addFoodDialog.show(getActivity()
                        .getSupportFragmentManager(), "updatefood_dialog");
                return true;
            case R.string.text_hidefood:
                if (!food.isHidden()) {
                    hideFood();
                }
                return true;
            case R.string.text_showfood:
                if (food.isHidden()) {
                    showFood();
                }
                return true;
            case R.string.txt_delfood:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFood() {
        plzw8Dialog.show();
        food.setHidden(false);
        String key = food.getFoodID();
//        Toast.makeText(StoreDeatailActivity.this,
//                key, Toast.LENGTH_SHORT).show();
        Food childFood = food;
        Map<String, Object> foodValue = childFood.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(getString(R.string.food_CODE)
                + key, foodValue);
        dbRef.updateChildren(childUpdate)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                item.setTitle(getString(R.string.text_hidestore));
                                pubMenu.clear();
                                FooddetailFragment.this.onCreateOptionsMenu(pubMenu, null);
                                plzw8Dialog.cancel();
                            }
                        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        plzw8Dialog.cancel();
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void hideFood() {
        new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setMessage(getString(R.string.txt_hideconfirm))
                .setPositiveButton(getString(R.string.txt_hide),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    final DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                plzw8Dialog.show();
                                food.setHidden(true);
                                String key = food.getFoodID();
//                                Toast.makeText(StoreDeatailActivity.this,
//                                        key, Toast.LENGTH_SHORT).show();
                                Food childFood = food;
                                Map<String, Object> foodValue = childFood.toMap();
                                Map<String, Object> childUpdate = new HashMap<>();
                                childUpdate.put(getString(R.string.food_CODE)
                                        + key, foodValue);
                                dbRef.updateChildren(childUpdate)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        pubMenu.clear();
                                                        FooddetailFragment.this.onCreateOptionsMenu(pubMenu, null);
                                                        plzw8Dialog.cancel();
                                                    }
                                                }).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                plzw8Dialog.cancel();
                                                Toast.makeText(getApplicationContext(),
                                                        e.getMessage(), Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            }
                        })
                .setNegativeButton(getString(R.string.text_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                .show();
    }

    @Override
    public void onDetach() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources()
                    .getColor(R.color.color_notify_reportfood));
        }
        super.onDetach();
    }

}
