package com.example.temilola.nytimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.temilola.nytimes.Article;
import com.example.temilola.nytimes.ArticleAdapter;
import com.example.temilola.nytimes.EndlessRecyclerViewScrollListener;
import com.example.temilola.nytimes.FilterDialogFragment;
import com.example.temilola.nytimes.ItemClickSupport;
import com.example.temilola.nytimes.R;
import com.example.temilola.nytimes.SearchFilters;
import com.example.temilola.nytimes.SpacesItemDecoration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.OnFilterSearchListener{

    //EditText etQuery;
    //GridView gvResults;
   // Button btnSearch;
    //ProgressBar pb;
    ArrayList<Article> articles;
    //ArticleArrayAdapter adapter;
    ArticleAdapter adapter;
    String query;
    RecyclerView rvArticle;

    //@BindView(R.id.gvResults) GridView gvResults;
    //@BindView(R.id.pbLoading) ProgressBar pb;



    private SearchFilters mFilters;
    //private SwipeRefreshLayout swipeContainer;

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

        rvArticle= (RecyclerView)findViewById(R.id.rvArticle);
        //initialize article
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(this, articles);

        rvArticle.setAdapter(adapter);

        StaggeredGridLayoutManager layout= new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        layout.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvArticle.setLayoutManager(layout);

        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        rvArticle.addItemDecoration(decoration);
        rvArticle.setItemAnimator(new SlideInUpAnimator());

        /*// Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);*/


    ItemClickSupport.addTo(rvArticle).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        //create an intent to display the article
                        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                        //get the article to display
                        Article article = articles.get(position);
                        //pass in article into the intent
                        i.putExtra("data", article);
                        //launch the activity
                        startActivity(i);
                    }
                }
        );

        // Add the scroll listener
        rvArticle.addOnScrollListener(new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreData(page);
            }
        });


    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP

        AsyncHttpClient client= new AsyncHttpClient();
        String url= "https://api.nytimes.com/svc/topstories/v2/home.json";

        RequestParams params= new RequestParams();
        params.put("api-key", "0be89842a4dc4f99ba0d5aa314659d4d");


        client.get(url, params, new JsonHttpResponseHandler(){

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear();
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults= null;
                try{
                    //pb.setVisibility(ProgressBar.INVISIBLE);
                    articleJsonResults= response.getJSONArray("results");
                    articles.clear();
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    // Remove all books from the adapter
                    //articles.clear();
                    Log.d("DEBUG", articles.toString());
                    //adapter.notifyDataSetChanged();
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });

        // Now we call setRefreshing(false) to signal refresh has finished
        //swipeContainer.setRefreshing(false);
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
        params.put("api-key", "0be89842a4dc4f99ba0d5aa314659d4d");
        params.put("page", offset);
        modifyFilterParams(params);

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults= null;

                try{
                   // pb.setVisibility(ProgressBar.INVISIBLE);
                    articleJsonResults= response.getJSONObject("response").getJSONArray("docs");
                    ArrayList<Article> moreArticles= new ArrayList<>();
                    moreArticles = Article.fromJSONArray(articleJsonResults);
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
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
        params.put("api-key", "0be89842a4dc4f99ba0d5aa314659d4d");


        client.get(url, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response ) {

                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults= null;

                try{
                    //pb.setVisibility(ProgressBar.INVISIBLE);
                    articleJsonResults= response.getJSONArray("results");
                    articles.clear();
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    // Remove all books from the adapter
                    //articles.clear();
                    Log.d("DEBUG", articles.toString());
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
        params.put("api-key", "0be89842a4dc4f99ba0d5aa314659d4d");
        params.put("page", 0);
        params.put("q", query);
        modifyFilterParams(params);

       // pb = (ProgressBar) findViewById(R.id.pbLoading);
       // pb.setVisibility(ProgressBar.VISIBLE);
        // begin_date=20160112&sort=oldest&fq=news_desk:("Arts"%20"Health")
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults= null;

                try{
                   // pb.setVisibility(ProgressBar.INVISIBLE);
                    articleJsonResults= response.getJSONObject("response").getJSONArray("docs");
                    articles.clear();
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    // Remove all books from the adapter
                   // adapter.clear();
                    //adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    Log.d("DEBUG", articles.toString());
                    //adapter.notifyDataSetChanged();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

}
