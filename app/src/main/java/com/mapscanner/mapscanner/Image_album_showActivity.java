package com.mapscanner.mapscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Image_album_showActivity extends AppCompatActivity {
    private LinearLayout linear;
    private ImageView picture1;
    private File imgFile;
    private Uri imageUri;
    private Button Return_page;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Button choose;
    private Button cancel;
    private Button upload;
    private Button all;
    protected List<File> selectedFile = new ArrayList<>();

    private RecyclerView rv;

    //接受前一个Intent传入的id
    private Bundle bundle;
    private int Show_Choice;

    // 获取屏幕尺寸
    DisplayMetrics outMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_album_show);
        linear = findViewById(R.id.Linear);
        picture1 = findViewById(R.id.V_Image1);
        Return_page = (Button) findViewById(R.id.Return_Back_to_page1);
        choose = findViewById(R.id.choose);
        cancel = findViewById(R.id.cancel);
        upload = findViewById(R.id.upload);
        all = findViewById(R.id.allChoose);
        bundle = this.getIntent().getExtras();
        Show_Choice = bundle.getInt("id");
        final StringBuilder[] flag = new StringBuilder[2];
        rv = (RecyclerView) findViewById(R.id.RcV);

        Return_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Image_album_showActivity.this,
                        MainActivity.class);//也可以这样写intent.setClass(MainActivity.this, OtherActivity.class);
                startActivity(intent);
            }
        });
        //接收Intent传递的id值，并判断，照相功能为1，打开相册功能为2
        switch (Show_Choice) {
            //如果传递为TAKE_PHOTO
            case TAKE_PHOTO: {
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
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("'img'_yyMMdd_HHmmss");
                File path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsoluteFile();
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~" + path);
                imgFile = new File(path, dateFormat.format(date) + ".jpg");
                try {
                    if (imgFile.exists()) {
                        imgFile.delete();
                    }
                    imgFile.createNewFile();
                    System.out.println(imgFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //判断版本号
                if (Build.VERSION.SDK_INT < 24) {
                    imageUri = Uri.fromFile(imgFile);
                } else {
                    imageUri = FileProvider.getUriForFile(Image_album_showActivity.this,
                            "com.MapScanner.MapScanner", imgFile);
                }
                // 启动相机程序
                Intent intent = new Intent();
                // 指定开启系统相机的Action
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                System.out.println("看着路：" + intent);
                startActivityForResult(intent, 1);

            }
            break;
            //如果传递为CHOOSE_PHOTO
            case CHOOSE_PHOTO: {
                upload.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
                all.setVisibility(View.INVISIBLE);
                if (Build.VERSION.SDK_INT >= 23) {
                    int write = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int read = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                300);
                    } else {
                        String name = "CrashDirectory";
                        File file1 = new File(Environment.getExternalStorageDirectory(), name);
                        if (file1.mkdirs()) {
                            Log.i("wytings", "permission -------------> " + file1.getAbsolutePath());
                        } else {
                            Log.i("wytings", "permission -------------fail to make file ");
                        }
                    }
                } else {
                    Log.i("wytings", "------------- Build.VERSION.SDK_INT < 23 ------------");
                }
                System.out.println("哈哈哈哈哈哈哈" + 666);
                linear.removeView(picture1);

                List<File> photoList = new ArrayList<>(Arrays.asList(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).listFiles()));
                for (Iterator it = photoList.iterator(); it.hasNext(); ) {
                    String fileName = ((File) it.next()).getName();

                    if (!(fileName.endsWith(".jpg") || fileName.endsWith(".JPG") || fileName.endsWith(
                            ".png") || fileName.endsWith(".PNG") || fileName.endsWith(".jpeg") || fileName.endsWith(
                            ".JPEG"))) {
                        it.remove();
                    }
                }
                System.out.println("哈哈哈哈哈哈哈" + photoList);
                RelativeLayout rtv = findViewById(R.id.rtv);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                rv.setLayoutManager(layoutManager);
                flag[0] = new StringBuilder("NO");
                flag[1] = new StringBuilder("IS");
                PhotoAdapter adapter = new PhotoAdapter(photoList, selectedFile, flag, rtv);
                rv.setAdapter(adapter);


                choose.setOnClickListener(i -> {
                    rtv.findViewById(R.id.Return_Back_to_page1).setVisibility(View.INVISIBLE);
                    rtv.findViewById(R.id.cancel).setVisibility(View.VISIBLE);
                    rtv.findViewById(R.id.upload).setVisibility(View.VISIBLE);
                    rtv.findViewById(R.id.allChoose).setVisibility(View.VISIBLE);
                    rtv.findViewById(R.id.choose).setVisibility(View.INVISIBLE);
                    flag[0].append("toOK");
                    System.out.println(flag);
                    PhotoAdapter adapter2 = new PhotoAdapter(photoList, selectedFile, flag, rtv);
                    rv.setAdapter(adapter2);
                });

                all.setOnClickListener(i -> {
//                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    if(flag[1].toString().equals("IS")){
                        flag[1].append("ALL");
                    }
                    selectedFile = photoList;
                    System.out.println(selectedFile);
                    PhotoAdapter adapter2 = new PhotoAdapter(photoList, selectedFile, flag, rtv);
                    rv.setAdapter(adapter2);
                    all.setEnabled(false);
                });

                cancel.setOnClickListener(i -> {
                    cancel.setVisibility(View.INVISIBLE);
                    Return_page.setVisibility(View.VISIBLE);
                    choose.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.INVISIBLE);
                    all.setEnabled(true);
                    all.setVisibility(View.INVISIBLE);
                    selectedFile = new ArrayList<>();
                    System.out.println(selectedFile);
                    flag[0] = new StringBuilder("NO");
                    flag[1] = new StringBuilder("IS");
                    PhotoAdapter adapter2 = new PhotoAdapter(photoList, selectedFile, flag, rtv);
                    rv.setAdapter(adapter2);
                });
            }
            break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        linear.removeView(rv);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            cancel.setVisibility(View.INVISIBLE);
            choose.setVisibility(View.INVISIBLE);
            all.setVisibility(View.INVISIBLE);
            try {
                bitmap =
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            picture1.setImageBitmap(bitmap);
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            int screenWidth = outMetrics.widthPixels;
            int screenHeight = outMetrics.heightPixels;
            picture1.setMaxWidth(screenWidth);
            picture1.setMaxHeight(screenHeight * 3);

            try {
                //图片插入到系统图库
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                        imgFile.getName(), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //通知图库刷新
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + imgFile.getAbsolutePath())));
        }
    }

    private void savePhotoToSDCard(String path, Bitmap photoBit) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'img'_yyMMdd_HHmmss");
        String photoName = dateFormat.format(date) + ".jpg";
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //在指定路径下创建文件
            File photoFile = new File(dir, photoName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBit != null) {
                    if (photoBit.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~:" + fileOutputStream);
//                        fileOutputStream.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClickUpload(View view) {
        switch (Show_Choice) {
            case TAKE_PHOTO: {
                Bitmap bitmap = ((BitmapDrawable) picture1.getDrawable()).getBitmap();
                uploadServer(bitmap);
            }
            break;
            case CHOOSE_PHOTO: {
                System.out.println(selectedFile);
                if (selectedFile.size() != 0) {
                    for (File file : selectedFile) {
                        try {
                            Bitmap bitmap =
                                    BitmapFactory.decodeStream(
                                            getContentResolver().openInputStream(Uri.fromFile(file)));
                            uploadServer(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    private boolean uploadServer(Bitmap bitmap) {
        boolean isSuccess = false;
        // TODO 上传服务器代码
        return isSuccess;
    }
}
