package com.mapscanner.mapscanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapscanner.mapscanner.baidubrain.FaceRecogniseUtil;
import com.mapscanner.mapscanner.utils.ImgUtil;
import com.mapscanner.mapscanner.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

public class FaceMatchActivity extends AppCompatActivity {

    private Button back;
    private Button match;
    private TextView tv01;
    private TextView tv02;
    private ImageView img01;
    private ImageView img02;
    private EditText matchRs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_match);

        back = findViewById(R.id.back_match);
        match = findViewById(R.id.start_match);
        tv01 = findViewById(R.id.match_text01);
        tv02 = findViewById(R.id.match_text02);
        img01 = findViewById(R.id.match_img01);
        img02 = findViewById(R.id.match_img02);
        matchRs = findViewById(R.id.sim_result);

        back.setOnClickListener(i -> {
            Intent intent = new Intent();
            intent.setClass(FaceMatchActivity.this, MainPageActivity.class);
            startActivity(intent);
        });

        tv01.setOnClickListener(i -> {
            if (Build.VERSION.SDK_INT >= 23) {
                int write = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int read = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            300);
                }
            } else {
                Log.i("wytings", "------------- Build.VERSION.SDK_INT < 23 ------------");
            }
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        tv02.setOnClickListener(i -> {
            if (Build.VERSION.SDK_INT >= 23) {
                int write = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int read = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            300);
                }
            } else {
                Log.i("wytings", "------------- Build.VERSION.SDK_INT < 23 ------------");
            }
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, 2);
        });

        match.setOnClickListener(i -> {
            StringBuffer rs = new StringBuffer();
            // 处理imgview的照片
            Bitmap bitmap1 = ((BitmapDrawable)img01.getDrawable()).getBitmap();
            Bitmap bitmap2 = ((BitmapDrawable)img02.getDrawable()).getBitmap();
            String base64One = ImgUtil.bitmapToBase64(bitmap1);
            String base64Two = ImgUtil.bitmapToBase64(bitmap2);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String accessToken = FaceRecogniseUtil.getAuth();
                    String matchResult = FaceRecogniseUtil.faceMatch(accessToken, base64One, base64Two);
                    try {
                        JSONObject jo = new JSONObject(matchResult);
                        String error = jo.get("error_msg").toString();
                        if (error.equals("SUCCESS")){
                            String resut = new JSONObject(jo.get("result").toString()).toString();
                            String sim = new JSONObject(resut).get("score").toString();
                            rs.append(sim);
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast(getApplicationContext(), "检测失败" + error, 0);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            System.out.println("1+++++++++++++++++++++++++++++++");
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2+++++++++++++++++++++++++++++++");
            if (!("".equals(rs.toString()))){
                matchRs.setText(rs.toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap bitmap = null;
            //判断手机系统版本号
            if (Build.VERSION.SDK_INT >= 19) {
                //4.4及以上系统使用这个方法处理图片
                bitmap = ImgUtil.handleImageOnKitKat(this, data);        //ImgUtil是自己实现的一个工具类
            } else {
                //4.4以下系统使用这个方法处理图片
                bitmap = ImgUtil.handleImageBeforeKitKat(this, data);
            }
            if (requestCode == 1){
                img01.setImageBitmap(bitmap);
            }
            if (requestCode == 2){
                img02.setImageBitmap(bitmap);
            }
        }
    }
}
