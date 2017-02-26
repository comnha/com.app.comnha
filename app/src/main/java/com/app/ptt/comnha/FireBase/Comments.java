package com.app.ptt.comnha.FireBase;

import android.content.Context;
import android.widget.Toast;

import com.app.ptt.comnha.Modules.Times;
import com.app.ptt.comnha.Interfaces.Transactions;
import com.app.ptt.comnha.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 10/6/2016.
 */

public class Comments implements Transactions {
    String content, userID, postID;
    Context context;
    Firebase ref;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Comments() {
    }

    @Override
    public void setupFirebase() {
        Firebase.setAndroidContext(context);
        ref = new Firebase(context.getResources().getString(R.string.firebase_path));
    }

    boolean check;

    @Override
    public boolean createNew() {
        setupFirebase();
        Comment newComment = new Comment();
        newComment.setContent(content);
        newComment.setUserID(userID);
        newComment.setDate(new Times().getDate());
        newComment.setTime(new Times().getTime());
        ref.child(context.getResources().getString(R.string.comments_CODE)).push().setValue(newComment, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    check = false;
                    Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    check = true;
                    Map<String, Object> comment = new HashMap<String, Object>();
                    comment.put(firebase.getKey(), true);
                    ref.child(context.getResources().getString(R.string.postcomment_CODE) + "/" + postID).updateChildren(comment);
                }
            }
        });
        return check;
    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }
}
