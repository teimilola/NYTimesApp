package com.example.temilola.nytimes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by temilola on 6/20/16.
 */
public class Article implements Parcelable{
    String webUrl;
    String headline;
    String thumbNail;


    public String getThumbNail() {
        return thumbNail;
    }

    public String getHeadline() {
        return headline;
    }

    public String getWebUrl() {
        return webUrl;
    }

    @Override
    public void writeToParcel(Parcel out, int flags){
        out.writeString(webUrl);
        out.writeString(headline);
        out.writeString(thumbNail);
    }

    private Article(Parcel in){
        webUrl= in.readString();
        headline= in.readString();
        thumbNail= in.readString();
    }

    public Article(){

    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator<Article> CREATOR= new Parcelable.Creator<Article>(){
        @Override
        public Article createFromParcel(Parcel in){
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size){
            return new Article[size];
        }
    };

    public Article(JSONObject jsonObject){
        try{
            JSONArray multimedia= jsonObject.getJSONArray("multimedia");
            if(jsonObject.has("web_url")) {
                this.webUrl = jsonObject.getString("web_url");
                this.headline = jsonObject.getJSONObject("headline").getString("main");
                if(multimedia.length() >0){
                    JSONObject multimediaJson= multimedia.getJSONObject(0);
                    this.thumbNail= "http://www.nytimes.com/" + multimediaJson.getString("url");
                }else{
                    this.thumbNail= "";
                }
            }else{
                this.webUrl=jsonObject.getString("url");
                this.headline= jsonObject.getString("title");
                if(multimedia.length() >0){
                    JSONObject multimediaJson= multimedia.getJSONObject(0);
                    this.thumbNail= multimediaJson.getString("url");
                }else{
                    this.thumbNail= "";
                }
            }
        } catch(JSONException e){

        }
    }


    public static ArrayList<Article> fromJSONArray(JSONArray array){
        ArrayList<Article> results= new ArrayList<>();

        for(int x=0; x< array.length(); x++){
            try{
                results.add(new Article(array.getJSONObject(x)));
            }catch(JSONException e){
                e.printStackTrace();
            }

        }
        return results;
    }
}
