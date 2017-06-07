package com.app.ptt.comnha.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.ptt.comnha.Models.FireBase.User;

/**
 * Created by PTT on 6/8/2017.
 */

public class MyDbHelper extends SQLiteOpenHelper {
    Context context;
    public static String DB_NAME = "db_comnha";
    private static String TABLE_NAME = "tb_user";
    private static String ID = "userid";
    private static String EMAIL = "email";
    private static String ROLE = "role";
    private static String TAG = "sqlitedb";

    public MyDbHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery = "CREATE TABLE" + TABLE_NAME + "(" +
                ID + "TEXT PRIMARY KEY," +
                EMAIL + "TEXT," +
                ROLE + "INTEGER)";
        try {
            sqLiteDatabase.execSQL(sqlQuery);
        } catch (Exception mess) {
            Log.e(TAG, mess.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void saveUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, user.getuID());
        values.put(EMAIL, user.getEmail());
        values.put(ROLE, user.getRole());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public int getRole() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{ID, EMAIL, ROLE};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        int role;
        if (cursor != null) {
            cursor.moveToFirst();
            role = cursor.getInt(3);
        } else {
            return 0;
        }
        cursor.close();
        db.close();
        return role;
    }

    public void updateUser(User newuser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
//        values.put(ID,);
    }
}
