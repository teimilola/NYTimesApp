package com.example.temilola.nytimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.temilola.nytimes.Article;
import com.example.temilola.nytimes.R;

public class ArticleActivity extends AppCompatActivity {

    private ShareActionProvider miShareAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Article article= getIntent().getParcelableExtra("data");
        final ProgressBar pbLoad = (ProgressBar) findViewById(R.id.pbLoad);
        pbLoad.setVisibility(ProgressBar.VISIBLE);


        String url= article.getWebUrl();
       //String url = getIntent().getStringExtra("url");

        getSupportActionBar().setTitle(article.getHeadline());

        WebView webView= (WebView) findViewById(R.id.wvArticle);

       webView.setWebViewClient(new WebViewClient(){
           @Override
           public boolean shouldOverrideUrlLoading(WebView view, String url) {
               pbLoad.setVisibility(ProgressBar.INVISIBLE);
               view.loadUrl(url);
               return true;
           }
       });
        webView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent= new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        //get reference to webView
        WebView wvView= (WebView) findViewById(R.id.wvArticle);
        //Pass in the URL currently being used by the WebView
        shareIntent.putExtra(Intent.EXTRA_TEXT, wvView.getUrl());

        miShareAction.setShareIntent(shareIntent);
        return super.onCreateOptionsMenu(menu);
    }
}
