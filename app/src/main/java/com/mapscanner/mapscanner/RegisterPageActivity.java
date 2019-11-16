package com.mapscanner.mapscanner;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapscanner.mapscanner.baidubrain.FaceRecogniseUtil;
import com.mapscanner.mapscanner.utils.ImgUtil;
import com.mapscanner.mapscanner.utils.MongoDBUtill;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterPageActivity extends AppCompatActivity {

    Button back;
    Button commit;
    EditText userName;
    EditText birthday;
    EditText phoneNum;
    EditText mail;
    ImageView photo;
    Button daySelect;
    Button imgSelect;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        back = findViewById(R.id.back_rig);
        commit = findViewById(R.id.commit);
        userName = findViewById(R.id.user_name);
        birthday = findViewById(R.id.birthday);
        phoneNum = findViewById(R.id.phone_num);
        mail = findViewById(R.id.e_mail);
        photo = findViewById(R.id.photo);
        daySelect = findViewById(R.id.day_select);
        imgSelect = findViewById(R.id.photo_button);

        birthday.setEnabled(false);

        // 返回按钮监听器
        back.setOnClickListener(i -> {
            Intent intent = new Intent();
            intent.setClass(RegisterPageActivity.this, MainPageActivity.class);
            startActivity(intent);
        });

        // 日前选择按钮监听器
        daySelect.setOnClickListener(i -> {
            Calendar calendar = Calendar.getInstance(Locale.CANADA);
            // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
            new DatePickerDialog(RegisterPageActivity.this, 4, new DatePickerDialog.OnDateSetListener() {
                // 绑定监听器(How the parent is notified that the date is set.)
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // 此处得到选择的时间，可以进行你想要的操作
                    birthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                }
            }
                    // 设置初始日期
                    , calendar.get(Calendar.YEAR)
                    , calendar.get(Calendar.MONTH)
                    , calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 注册图片选择按钮监听器
        imgSelect.setOnClickListener(i -> {
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
            Intent intent = new Intent();
            intent.setAction("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        // 注册提交按钮监听器
        commit.setOnClickListener(i -> {
            Editable uName = userName.getText();
            Editable birth = birthday.getText();
            Editable phone = phoneNum.getText();
            Editable eMail = mail.getText();
            Drawable drawable = photo.getDrawable();

            boolean judge =
                    "".equals(uName.toString()) || "".equals(birth.toString()) || "".equals(
                            eMail.toString()) || "".equals(phone.toString());

            if (judge || uName == null || birth == null || phone == null || eMail == null || drawable == null) {
                Toast t = Toast.makeText(RegisterPageActivity.this, "都是必填项，您有未填项", Toast.LENGTH_LONG);
                t.show();
            } else {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                String base64 = ImgUtil.bitmapToBase64(bitmap);
                Map<String, Object> map = new HashMap<>();
                map.put("user_name", uName.toString());
                map.put("birthday", birth.toString());
                map.put("phone_num", phone.toString());
                map.put("e-mail", eMail.toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 获取token对象
                        String accessToken = FaceRecogniseUtil.getAuth();
                        String search = FaceRecogniseUtil.facesSearch(accessToken, base64, "group01");
                        JSONObject searchJson = null;
                        try {
                            searchJson = new JSONObject(search);
                            String searchMsg = searchJson.get("error_msg").toString();

                            if (searchMsg.equals("SUCCESS")){
                                Looper.prepare();
                                Toast.makeText(RegisterPageActivity.this, "用户已注册，无需再注册", Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }else if (searchMsg.equals("match user is not found")) {
                                // 注册到百度大脑
                                String add = FaceRecogniseUtil.add(accessToken, base64, map.toString());
                                if (add == null) {
                                    Looper.prepare();
                                    Toast.makeText(RegisterPageActivity.this, "注册失败：注册异常", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                } else {
                                    JSONObject jsonObject = null;
                                    jsonObject = new JSONObject(add);
                                    String errorMsg = jsonObject.get("error_msg").toString();
                                    if (errorMsg.equals("SUCCESS")) {
                                        // 用户信息上传到云服务器的MongoDB
                                        Document document = new Document(map);
                                        document.append("photo", base64);
                                        MongoDatabase face_library = MongoDBUtill.getConnection("39.105.102.158",
                                                27017,
                                                "face_library");
                                        MongoCollection<Document> user_info = face_library.getCollection(
                                                "user_info");
                                        user_info.insertOne(document);
                                        Looper.prepare();
                                        Toast.makeText(RegisterPageActivity.this, "注册成功且上传数据到云数据库",
                                                Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    } else {
                                        Looper.prepare();
                                        Toast.makeText(RegisterPageActivity.this,
                                                new StringBuffer("注册失败：").append(errorMsg).toString(),
                                                Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }
                                }
                            }else {
                                Looper.prepare();
                                Toast.makeText(RegisterPageActivity.this, "注册失败：" + searchMsg, Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Bitmap bitmap = null;
            //判断手机系统版本号
            if (Build.VERSION.SDK_INT >= 19) {
                //4.4及以上系统使用这个方法处理图片
                bitmap = ImgUtil.handleImageOnKitKat(this, data);        //ImgUtil是自己实现的一个工具类
            } else {
                //4.4以下系统使用这个方法处理图片
                bitmap = ImgUtil.handleImageBeforeKitKat(this, data);
            }
            photo.setImageBitmap(bitmap);
        }
    }
}
