package com.example.temilola.nytimes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by temilola on 6/23/16.
 */
public class SearchFilters implements Parcelable, Serializable {
    String begin_date;
    String spinnerValue;
    ArrayList<String> news_desk;

    public ArrayList<String> getNews_desk() {
        return news_desk;
    }

    public String getSpinnerValue() {
        return spinnerValue;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public SearchFilters(){

    }

    @Override
    public void writeToParcel(Parcel out, int flags){
        out.writeString(begin_date);
        out.writeString(spinnerValue);
        out.writeStringList(news_desk);
    }

    private SearchFilters(Parcel in){
        begin_date= in.readString();
        spinnerValue= in.readString();
        news_desk= in.createStringArrayList();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator<SearchFilters> CREATOR= new Parcelable.Creator<SearchFilters>(){
        @Override
        public SearchFilters createFromParcel(Parcel in){
            return new SearchFilters(in);
        }

        @Override
        public SearchFilters[] newArray(int size){
            return new SearchFilters[size];
        }
    };


}
