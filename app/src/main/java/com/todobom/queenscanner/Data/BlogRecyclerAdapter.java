package com.todobom.queenscanner.Data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;
import com.todobom.queenscanner.R;

import java.sql.Array;
import java.util.Date;
import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private Context context;
//    private String [] blogList;
    private List<String> blogList;
    private List<String> dateList;
    OnImageclickListener monImageclickListener;

    public BlogRecyclerAdapter() {
    }

    public BlogRecyclerAdapter(Context context, List<String> blogList, List<String> date, OnImageclickListener onImageclickListener) {
        this.context = context;
        this.blogList = blogList;
        this.dateList = date;
        this.monImageclickListener = onImageclickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_future_save_row,parent,false);

        return new ViewHolder(view, context,monImageclickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        String imageUrl = blogList.get(position).toString();


//        Log.d("taggg",""+dat);

       // TODO: Use Picasso library to load image

        Picasso.get().load(""+imageUrl).into(holder.image);

//        Picasso.with(context).load(imageUrl).into(holder.image);  // used in old dependencies

    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView image;
//        public TextView datee;

        OnImageclickListener onImageclickListener;

        public ViewHolder(@NonNull View view, Context ctx, OnImageclickListener onImageclickListener) {
            super(view);

            this.onImageclickListener = onImageclickListener;

            context = ctx;
            image = view.findViewById(R.id.postImageList);
//            datee = view.findViewById(R.id.postImagedate);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onImageclickListener.onImageclick(getAdapterPosition());
        }
    }
    public interface OnImageclickListener{
        void onImageclick(int position);
    }


}
