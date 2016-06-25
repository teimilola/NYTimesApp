package com.example.temilola.nytimes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by temilola on 6/24/16.
 */
public class ArticleResponse {

    List<Article> articles;

    // public constructor is necessary for collections
    public ArticleResponse() {
        articles = new ArrayList<Article>();
    }

    public static ArticleResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        ArticleResponse articleResponse = gson.fromJson(response, ArticleResponse.class);
        return articleResponse;
    }
}
