package com.example.temilola.nytimes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by temilola on 6/24/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {


    private Context mContext;
    private List<Article> mArticles;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView title;
        public ImageView Image;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            this.title = (TextView) itemView.findViewById(R.id.tvTitle);
            this.Image = (ImageView) itemView.findViewById(R.id.ivImage);

        }
    }



    public ArticleAdapter(Context context, List<Article> article) {
        this.mArticles = article;
        this.mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder( articleView);
        return viewHolder;
    }

    // Clean all elements of the recycler
    public void clear() {
        mArticles.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Article> list) {
        mArticles.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        // Set item views based on your views and data model
        TextView tvTitle = viewHolder.title;
        tvTitle.setText(article.getHeadline());
        final ImageView ivImage= viewHolder.Image;

        //clear out recycled image from convertView from last time
        ivImage.setImageResource(0);

        //populate the thumbnail image
        //remote download image in the background
        String thumbNail= article.getThumbNail();

        if(!TextUtils.isEmpty(thumbNail)){
            //Glide.with(getContext()).load(thumbNail).into(ivImage);


            Glide.with(getContext()).load(thumbNail).bitmapTransform(new RoundedCornersTransformation(getContext(),10, 10))
                    .fitCenter().into(ivImage);
        }
        else{
            Glide.with(getContext()).load(R.drawable.news_anchors).asGif().fitCenter().into(ivImage);
        }

    }



    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

}
