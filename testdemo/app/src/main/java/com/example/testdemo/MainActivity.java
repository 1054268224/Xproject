package com.example.testdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testdemo.TrafficaBallView;

public class MainActivity extends AppCompatActivity {

    private Context  mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusBar();
        mContext = this;
        setContentView(R.layout.switch_view);
        final Switch switchMsg = findViewById(R.id.switchMsg);

        switchMsg.setChecked(false);
        switchMsg.setTextAppearance(mContext,R.style.s_false);

        switchMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked=true){
                    switchMsg.setTextAppearance(mContext,R.style.s_true);
                }else{
                    switchMsg.setTextAppearance(mContext,R.style.s_false);
                }
            }
        });

        switchMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchMsg.isChecked()==true){
                    Toast.makeText(mContext,"开启消息提醒",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"关闭消息提醒",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*#############################################TrafficBallView Start####################################*/

/*
    private TrafficaBallView mCircleView;
    private SeekBar mSeekBar;
    private TextView power;
    private int max = 1024;
    private int min = 102;

  */
/*  private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                int num = msg.getData().getInt("progress");
                Log.i("num", num + "");
                power.setText((float) num / 100 * max + "M/" + max + "M");
            }
        }
    };*//*


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        */
/*getActionBar().hide();*//*

        setContentView(R.layout.traffic_ball_view);

        power = (TextView) findViewById(R.id.power);
        */
/*power.setText(min + "M/" + max + "M");*//*


        mCircleView = (TrafficaBallView) findViewById(R.id.wave_view);
        // 设置多高，float，0.1-1F
        mCircleView.setmWaterLevel(0.5F);
        // 开始执行
        mCircleView.startWave();

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCircleView.setmWaterLevel((float) progress / 100);
                mCircleView.setUsedFlow((float) progress / 100*1024);
                // 创建一个消息
                */
/*Message message = new Message();
                Bundle bundle = new Bundle();
                // put一个int值
                bundle.putInt("progress", progress);
                // 装载
                message.setData(bundle);
                // 发送消息
                handler.sendMessage(message);
                // 创建表示
                message.what = 1;*//*

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        transparentStatusBar();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mCircleView.stopWave();
        mCircleView = null;
        super.onDestroy();
    }
*/

    /*#############################################TrafficBallView End####################################*/

    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}