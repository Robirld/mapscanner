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

import com.mapscanner.mapscanner.baidubrain.FaceRecogniseUtil;
import com.mapscanner.mapscanner.utils.ImgUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PhotoAdapter extends RecyclerView.Adapter {

    private final List<String> photoList;

    public PhotoAdapter(List<String> photoList) {
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

        String file = photoList.get(position);
        ViewHolder holder = (ViewHolder)vholder;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            holder.imgView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuffer str = new StringBuffer();
        String base64 = ImgUtil.bitmapToBase64(bitmap);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String accessToken = FaceRecogniseUtil.getAuth();
                String msg = FaceRecogniseUtil.faceDetect(accessToken, base64);
                try {
                    JSONObject jo = new JSONObject(msg);
                    String error = jo.get("error_msg").toString();
                    if (error.equals("SUCCESS")){
                        JSONObject result = new JSONObject(jo.get("result").toString());
                        JSONArray fl = (JSONArray) result.get("face_list");
                        Map<String, Object> map = new HashMap<>();
                        map.put("人脸数", result.get("face_num"));
                        for (int i = 0; i < fl.length(); i++){
                            int j = i +1;
                            JSONObject fi = new JSONObject(fl.get(i).toString());
                            fi.remove("face_token");
                            fi.remove("location");
                            fi.remove("face_probability");
                            fi.remove("angle");
                            Map<String, Object> info = new HashMap<>();
                            info.put("年龄", fi.get("age"));
                            info.put("颜值", fi.get("beauty"));
                            info.put("性别", new JSONObject(fi.get("gender").toString()).get("type"));
                            info.put("种族", new JSONObject(fi.get("race").toString()).get("type"));
                            info.put("脸型", new JSONObject(fi.get("face_shape").toString()).get("type"));
                            map.put(""+j, info);
                        }
                        str.append(map);
                    }else {
                        str.append("检测失败：" + error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        holder.text.setText(str);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

}
