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
    private final RelativeLayout rtv;
    private StringBuilder flag[] = new StringBuilder[2];
    private File file;
    private String[] strs;
    private int[] status;
    private List<File> selectedFile;

    public PhotoAdapter(List<File> photoList, List<File> selectedFile, StringBuilder[] flag, RelativeLayout rtv) {
        this.photoList = photoList;
        this.flag = flag;
        this.selectedFile = selectedFile;
        this.rtv = rtv;

        status = new int[photoList.size()];

        strs = new String[photoList.size()];
        for (int i = 0; i<strs.length; i++) {
            if (flag[1].toString().equals("IS")){
                strs[i] = new String("NO");
            }else if (flag[1].toString().equals("ISALL")){
                strs[i] = new String("SELECTED");
            }

            if(flag[0].toString().equals("NO")){
                status[i] = View.INVISIBLE;
            }else if (flag[0].toString().equals("NOtoOK")){
                status[i] = View.VISIBLE;
            }
        }

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
        holder.text.setText(strs[position]);
        holder.text.setVisibility(status[position]);

        holder.imgView.setOnClickListener(i -> {
            if (flag[0].toString().equals("NOtoOK")){
                if (holder.text.getText().toString().equals("NO")){
                        selectedFile.add(photoList.get(position));
                        holder.text.setText("SELECTED");
                        strs[position] = new String("SELECTED");
                        System.out.println(selectedFile);
                }else if (holder.text.getText().toString().equals("SELECTED")){
                        selectedFile.remove(photoList.get(position));
                        System.out.println(selectedFile);
                        holder.text.setText("NO");
                        strs[position] = new String("NO");
                }
                if (!(rtv.findViewById(R.id.allChoose).isEnabled())){
                    rtv.findViewById(R.id.allChoose).setEnabled(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

}
