package com.example.sessionexample;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter {

    private Context context;
    private List<String> imageUris,postIDs;
    private String getUserId;
    public ImageGridAdapter(Context context,String getUserId) {
        this.context = context;
        imageUris = new ArrayList<>();
        postIDs = new ArrayList<>();
        this.getUserId = getUserId;
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardView cardView;
        if (convertView == null) {
            cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.grid_item_with_card, parent, false);
        } else {
            cardView = (CardView) convertView;
        }

        ImageView imageView = cardView.findViewById(R.id.gridImageView);
        Picasso.get().load(imageUris.get(position)).into(imageView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailScrollActivity.class);
                intent.putStringArrayListExtra("imageUris", (ArrayList<String>) imageUris);
                intent.putStringArrayListExtra("postids", (ArrayList<String>) postIDs);
                intent.putExtra("position", position);
                intent.putExtra("userId",getUserId);
                context.startActivity(intent);
            }
        });

        return cardView;
    }
    public void addImageUri(String imageUri) {
        imageUris.add(imageUri);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public void addPostIds(String postId)
    {
        postIDs.add(postId);
    }

}


