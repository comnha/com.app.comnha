package com.app.ptt.comnha.Modules;

import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;

import java.util.Comparator;

/**
 * Created by ciqaz on 09/07/2017.
 */

public class orderByPostHightLight implements Comparator<Post> {

    @Override
    public int compare(Post o1, Post o2) {
        return (o1.getUserComment().size() < o2.getUserComment().size() ? 1 :
                o1.getUserComment().size() > o2.getUserComment().size() ? -1 : 0);

    }
}