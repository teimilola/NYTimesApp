package com.example.temilola.nytimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.example.temilola.nytimes.FilterDialogFragment;
import com.example.temilola.nytimes.R;
import com.example.temilola.nytimes.SearchFilters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.OnFilterSearchListener{

    //EditText etQuery;
    //GridView gvResults;
   // Button btnSearch;
    //ProgressBar pb;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    String query;

    @BindView(R.id.gvResults) GridView gvResults;
    @BindView(R.id.pbLoading) ProgressBar pb;



    private SearchFilters mFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mFilters = new SearchFilters();
        setupViews();
        getTopStories();

    }

    public void setupViews(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    public void onUpdateFilters(SearchFilters filters) {
        // Access the updated filters here and store in member variable
        // Triggers a new search with these filters updated
        mFilters  = filters;
        initialSearch();
    }

    // Append more data into the adapter
    public void loadMoreData(int offset){
        AsyncHttpClient client = new AsyncHttpClient();
        String url= "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params= new RequestParams();
        params.put("api-key", "7d3a8531942b426897bde5875df30e86");
        params.put("page", offset);
        modifyFilterParams(params);

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

    private void modifyFilterParams(RequestParams params) {
        if (mFilters.getNews_desk() != null) {
            String newsDeskItemsStr =
                    android.text.TextUtils.join(" ", mFilters.getNews_desk());
            String newsDeskParamValue =
                    String.format("news_desk:(%s)", newsDeskItemsStr);
            params.put("fq", newsDeskParamValue);
        }
        if (mFilters.getBegin_date()!= null){
            String beginDateParamValue= mFilters.getBegin_date();
            params.put("begin_date", beginDateParamValue);
        }
        if (mFilters.getSpinnerValue()!= null){
            String sortParamValue= mFilters.getSpinnerValue();
            params.put("sort", sortParamValue);
        }
    }

    //Top Stories
    public void getTopStories(){

        AsyncHttpClient client= new AsyncHttpClient();
        String url= "https://api.nytimes.com/svc/topstories/v2/home.json";

        RequestParams params= new RequestParams();
        params.put("api-key", "7d3a8531942b426897bde5875df30e86");

        client.get(url, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response ) {

                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults= null;

                try{
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    articleJsonResults= response.getJSONArray("results");
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

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem= menu.findItem(R.id.action_search);
        final MenuItem settings= menu.findItem(R.id.action_settings);
        final SearchView searchView= (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                query = q;
                //perform query here
                initialSearch();
                //workaround keyboard issues
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //Setup Advanced search filters
        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showFilterDialog();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void initialSearch(){
        //perform query here
        AsyncHttpClient client = new AsyncHttpClient();
        String url= "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params= new RequestParams();
        params.put("api-key", "7d3a8531942b426897bde5875df30e86");
        params.put("page", 0);
        params.put("q", query);
        modifyFilterParams(params);

        pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        // begin_date=20160112&sort=oldest&fq=news_desk:("Arts"%20"Health")
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


   private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance(mFilters);
        filterDialogFragment.show(fm, "fragment_settings_menu");
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
