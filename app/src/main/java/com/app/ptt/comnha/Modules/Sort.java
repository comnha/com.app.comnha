package com.app.ptt.comnha.Modules;

import com.app.ptt.comnha.Models.FireBase.Post;

import java.util.ArrayList;

public class Sort {

    private ArrayList<Post> array;
    private ArrayList<Post> tempMergArr;
    private ArrayList<Post> finalArr;
    private int length;
    public ArrayList<Post> sort(ArrayList<Post> inputArr) {
        this.array = inputArr;
        this.length = inputArr.size();
        this.tempMergArr =new ArrayList<>(length);
        Post pos;
//        post.setLocaName("NULL  ");
        for (Post post1: array){
            tempMergArr.add(post1);
        }
        doMergeSort(0, length - 1);
        return array;
    }

    private void doMergeSort(int lowerIndex, int higherIndex) {

        if (lowerIndex < higherIndex) {
            int middle = lowerIndex + (higherIndex - lowerIndex) / 2;
            // Below step sorts the left side of the array
            doMergeSort(lowerIndex, middle);
            // Below step sorts the right side of the array
            doMergeSort(middle + 1, higherIndex);
            // Now merge both sides
            mergeParts(lowerIndex, middle, higherIndex);
        }
    }

    private void mergeParts(int lowerIndex, int middle, int higherIndex) {

        for (int i = lowerIndex; i <= higherIndex; i++) {
            tempMergArr.set(i,array.get(i));
        }
        int i = lowerIndex;
        int j = middle + 1;
        int k = lowerIndex;
        while (i <= middle && j <= higherIndex) {
//            if (tempMergArr.get(i).getCommentCount() <= tempMergArr.get(j).getCommentCount()) {
//                array.set(k,tempMergArr.get(i));
//                i++;
//            } else {
//                array.set(k,tempMergArr.get(j));
//                j++;
//            }
//            k++;
        }
        while (i <= middle) {
            array.set(k,tempMergArr.get(i));
            k++;
            i++;
        }

    }

}
