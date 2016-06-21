package com.example.temilola.nytimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.temilola.nytimes.Article;
import com.example.temilola.nytimes.ArticleArrayAdapter;
import com.example.temilola.nytimes.EndlessScrollListener;
import com.example.temilola.nytimes.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    //EditText etQuery;
    GridView gvResults;
   // Button btnSearch;
    ProgressBar pb;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();


    }

    public void setupViews(){
        //etQuery= (EditText)findViewById(R.id.etQuery);
        gvResults= (GridView)findViewById(R.id.gvResults);
       // btnSearch= (Button)findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter= new ArticleArrayAdapter(this,articles);
        gvResults.setAdapter(adapter);

        //hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                //get the article to display
                Article article = articles.get(position);
                //pass in article into the intent
                i.putExtra("data", article);
                //launch the activity
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadMoreData(page);
                return true;
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });


    }

    // Append more data into the adapter
    public void loadMoreData(int offset){
        AsyncHttpClient client = new AsyncHttpClient();
        String url= "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params= new RequestParams();
        params.put("api-key", "7d3a8531942b426897bde5875df30e86");
        params.put("page", offset);

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults= null;

                try{
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    articleJsonResults= response.getJSONObject("response").getJSONArray("docs");
                    ArrayList<Article> moreArticles= new ArrayList<>();
                    moreArticles = Article.fromJSONArray(articleJsonResults);
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    Log.d("DEBUG", moreArticles.toString());
                    //adapter.notifyDataSetChanged();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem= menu.findItem(R.id.action_search);
        final SearchView searchView= (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //perform query here
                AsyncHttpClient client = new AsyncHttpClient();
                String url= "http://api.nytimes.com/svc/search/v2/articlesearch.json";

                RequestParams params= new RequestParams();
                params.put("api-key", "7d3a8531942b426897bde5875df30e86");
                params.put("page", 0);
                params.put("q", query);

                pb = (ProgressBar) findViewById(R.id.pbLoading);
                pb.setVisibility(ProgressBar.VISIBLE);

                client.get(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", response.toString());
                        JSONArray articleJsonResults= null;

                        try{
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            articleJsonResults= response.getJSONObject("response").getJSONArray("docs");
                            articles=Article.fromJSONArray(articleJsonResults);
                            // Remove all books from the adapter
                            adapter.clear();
                            adapter.addAll(Article.fromJSONArray(articleJsonResults));
                            Log.d("DEBUG", articles.toString());
                            //adapter.notifyDataSetChanged();
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
                //workaround keyboard issues
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*
    public void onArticleSearch(View view) {
        String query= etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        String url= "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params= new RequestParams();
        params.put("api-key", "7d3a8531942b426897bde5875df30e86");
        params.put("page", 0);
        params.put("q", query);

        pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults= null;

                try{
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    articleJsonResults= response.getJSONObject("response").getJSONArray("docs");
                    articles=Article.fromJSONArray(articleJsonResults);
                    // Remove all books from the adapter
                    adapter.clear();
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    Log.d("DEBUG", articles.toString());
                    //adapter.notifyDataSetChanged();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
    }*/
}
