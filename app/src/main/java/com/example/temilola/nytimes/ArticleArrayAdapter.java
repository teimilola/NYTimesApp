package com.example.temilola.nytimes;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by temilola on 6/20/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> arcticles) {
        super(context, android.R.layout.simple_list_item_1);
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Article article = this.getItem(position);
        //check to see if existing view is being re-used
        //not using a recycled view-> inflate the layout
        if (convertView== null){
            LayoutInflater inflater= LayoutInflater.from(getContext());
            convertView= inflater.inflate(R.layout.item_article_result, parent, false);
        }
        //find the image view
        ImageView imageView= (ImageView) convertView.findViewById(R.id.ivImage);

        //clear out recycled image from convertView from last time
        imageView.setImageResource(0);

        TextView tvTitle= (TextView)convertView.findViewById(R.id.tvTitle);

        tvTitle.setText(article.getHeadline());

        //populate the thumbnail image
        //remote download image in the background
        String thumbNail= article.getThumbNail();

        if(!TextUtils.isEmpty(thumbNail)){
            Picasso.with(getContext()).load(thumbNail).into(imageView);
        }

        return convertView;
    }
}

