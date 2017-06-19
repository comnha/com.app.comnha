package com.app.ptt.comnha.Utils;

import android.content.Context;
import android.util.Log;

import com.app.ptt.comnha.Models.FireBase.Post;
import com.app.ptt.comnha.Models.FireBase.Store;
import com.app.ptt.comnha.Models.MyLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuong on 11/7/2016.
 */

public class Storage {




    //public Storage(Context context){
       // this.mContext=context;
   // }
    public static void writeFile(Context mContext,String input,String filename){
        FileOutputStream outputStream;
        try {
            File file = new File(mContext.getCacheDir(),filename+".json");
            outputStream=new FileOutputStream(file);
            outputStream.write(input.getBytes());
            outputStream.close();
            //Toast.makeText(mContext,"save ok",Toast.LENGTH_LONG).show();
            Log.i("Storage.Save:","Save OK");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean deleteFile(Context mContext,String filename){

        File file = new File(mContext.getCacheDir(), filename+".json");
        if(file!=null) {
            file.delete();
            Log.i("Storage.deleteFile:","delete OK");
            return true;

        }else{
            return false;
        }
    }
    public static String readFile(Context mContext,String filename){

        try {
            if (mContext.getCacheDir() == null) {
                Log.i("Storage.Read:", "getCacheDir=null");
                Storage.writeFile(mContext, "", filename);

            } else {
                File file = new File(mContext.getCacheDir(), filename + ".json");
                //file.delete();
                if (file != null) {
                    FileInputStream inputStream = new FileInputStream(file);
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String s = null;
                    while ((s = br.readLine()) != null) {
                        sb.append(s);
                        sb.append("\n");
                    }
                    Log.i("Storage.Read:", "Read OK");
                    //Toast.makeText(mContext,sb.toString(),Toast.LENGTH_LONG).show();
                    return sb.toString();

                }
            }
            }catch(FileNotFoundException e){
                e.printStackTrace();

            }catch(IOException e) {
            e.printStackTrace();

        }catch (Exception e){
            e.printStackTrace();

        }

        return null;

    }
    public static ArrayList<MyLocation> readJSONMyLocation(String json){
        Gson gson=new Gson();
        ArrayList<MyLocation> locations =
                gson.fromJson(json,new TypeToken<ArrayList<MyLocation>>(){}.getType());
        if(locations.size()>0) {
            Log.i("readJSONPost", "locations.size() =" + locations.size());
            return locations;
        }
        return null;



    }
    public static ArrayList< Post > readJSONPost(String json) throws JSONException, ParseException {
        ArrayList<Post> posts=new ArrayList<>();

        if(posts.size()>0) {
            Log.i("readJSONPost","posts.size() ="+posts.size());
            return posts;

        }
        return null;
    }
    public static ArrayList<Post> readJSONPost1(String json)  {
        Gson gson=new Gson();
        ArrayList<Post> posts =
                gson.fromJson(json,new TypeToken<ArrayList<Post>>(){}.getType());
        if(posts.size()>0) {
            Log.i("readJSONPost", "posts.size() =" + posts.size());
            return posts;
        }
        return null;
    }
    public static String  parsePostToJson(ArrayList<Post> posts) throws IOException {
        Gson gson=new Gson();
        String json=gson.toJson(posts);
       // Log.i("json string:",json);
        if(json!=null)
        return json;
        return null;
    }


    public static String  parseMyLocationToJson(List<MyLocation> locations) {
        Gson gson=new Gson();
        String json1=gson.toJson(locations);
        //Log.i("json string:",json1);
        if(json1!=null)
            return json1;
        return null;
    }

}
