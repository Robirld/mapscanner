package com.mapscanner.mapscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

class PhotoAdapter extends RecyclerView.Adapter {

    private final List<File> photoList;
    private File file;

    public PhotoAdapter(List<File> photoList) {
        this.photoList = photoList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView text = null;
        ImageView imgView = null;

        public ViewHolder(@NonNull View view) {
            super(view);
            imgView = (ImageView) view.findViewById(R.id.my_img);
            text = view.findViewById(R.id.selected);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vholder, int position) {

        file = photoList.get(position);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~"+file);
        ViewHolder holder = (ViewHolder)vholder;

        try {
            holder.imgView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        holder.imgView.setOnClickListener(i -> {
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

}
