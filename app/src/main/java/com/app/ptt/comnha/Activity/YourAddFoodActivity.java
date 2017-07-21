package com.app.ptt.comnha.Activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.ptt.comnha.Adapters.Food_recycler_adapter;
import com.app.ptt.comnha.Const.Const;
import com.app.ptt.comnha.Models.FireBase.Food;
import com.app.ptt.comnha.Models.FireBase.User;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class YourAddFoodActivity extends AppCompatActivity {


    StorageReference stRef;
    DatabaseReference dbRef;
    RecyclerView rv_food;
    RecyclerView.LayoutManager foodLm;
    ArrayList<Food> foods;
    Food_recycler_adapter foodAdapter;
    ValueEventListener foodEventListener;
    User user = LoginSession.getInstance().getUser();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youraddfood);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Const.DATABASE_PATH);
        stRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Const.STORAGE_PATH);
        Ref();
        getAllImg();
    }

    private void Ref() {
        rv_food = (RecyclerView) findViewById(R.id.rv_img_youraddfood);
        foodLm = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL,
                false);
        rv_food.setLayoutManager(foodLm);
        foods = new ArrayList<>();
        foodAdapter = new Food_recycler_adapter(foods, this, stRef);
        rv_food.setAdapter(foodAdapter);
        foodAdapter.setOnItemClickLiestner(new Food_recycler_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(Food food, Activity activity, View itemView) {

            }
        });
    }

    private void getAllImg() {
        foodEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Food food = item.getValue(Food.class);
                    food.setFoodID(item.getKey());
                    foods.add(food);
                    foodAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.food_CODE))
                .orderByChild("isHidden_uID")
                .equalTo(false + "_" + user.getuID())
                .addListenerForSingleValueEvent(foodEventListener);
    }
}
