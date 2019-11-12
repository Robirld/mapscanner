package com.mapscanner.mapscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends AppCompatActivity {
    private Handler handler = new Handler(){
      @Override
      public void handleMessage(Message msg){
          super.handleMessage(msg);
          next();
      }
    };

    private void next() {
        Intent intent = new Intent(WelcomeActivity.this, MainPageActivity.class);
        startActivity(intent);

        //关闭当前
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        },5000);
    }

}
