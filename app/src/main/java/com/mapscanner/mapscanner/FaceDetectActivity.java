package com.mapscanner.mapscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class FaceDetectActivity extends AppCompatActivity {

    private Button back;
    private Button choose;
    private RecyclerView rcv;
    private final int REQUEST_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);
        back = findViewById(R.id.back_detect);
        choose = findViewById(R.id.detect_choose);
        rcv = findViewById(R.id.rcv);

        back.setOnClickListener(i -> {
            Intent intent = new Intent();
            intent.setClass(FaceDetectActivity.this, MainPageActivity.class);
            startActivity(intent);
        });

        choose.setOnClickListener(i -> {
            Intent intent = new Intent(FaceDetectActivity.this, MultiImageSelectorActivity.class);
            // 存储照片的list
            ArrayList<String> imgList = new ArrayList<>();
            // 是否显示调用相机拍照
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
            // 最大图片选择数量
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
            // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
            // 默认选择图片,回填选项(支持String ArrayList)
            intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, imgList);
            startActivityForResult(intent, REQUEST_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_IMAGE){
                // 获取返回的图片列表
                List<String> imgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                System.out.println(imgs);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                rcv.setLayoutManager(layoutManager);
                PhotoAdapter adapter = new PhotoAdapter(imgs);
                rcv.setAdapter(adapter);
            }
        }
    }
}
