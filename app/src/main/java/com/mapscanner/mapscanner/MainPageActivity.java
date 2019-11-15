package com.mapscanner.mapscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainPageActivity extends AppCompatActivity {

//    private final int REGISTER_FACE = 1;
//    private final int SEARCH_FACE = 2;

    private Button register;
    private Button search;
    private Button detect;
    private Button match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        register = findViewById(R.id.face_ragister);
        search = findViewById(R.id.face_search);
        detect = findViewById(R.id.face_detect);
        match = findViewById(R.id.face_match);

        register.setOnClickListener(i -> {
            Intent intent = new Intent();
            intent.setClass(MainPageActivity.this, RegisterPageActivity.class);
            startActivity(intent);
        });

        search.setOnClickListener(i -> {
            Intent intent = new Intent();
            intent.setClass(MainPageActivity.this, MainActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putInt("action", SEARCH_FACE);
//            intent.putExtras(bundle);
            startActivity(intent);
        });

        detect.setOnClickListener(i -> {
            Intent intent = new Intent();
            intent.setClass(MainPageActivity.this, FaceDetectActivity.class);
            startActivity(intent);
        });

        match.setOnClickListener(i -> {
            Intent intent = new Intent();
            intent.setClass(MainPageActivity.this, FaceMatchActivity.class);
            startActivity(intent);
        });
    }

}
