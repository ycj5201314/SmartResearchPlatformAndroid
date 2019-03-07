package com.example.administrator.partner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import Widget.Back;

/**
 * Created by Administrator on 2016/1/16.
 */
public class MeMessage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_message);
        NoShowKeyBoard();
        initButton();
        ((Back)findViewById(R.id.btn_back)).setContext(MeMessage.this);//返回按钮
    }

    //禁止弹出键盘
    private void NoShowKeyBoard(){
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    //按钮监听
    private void initButton(){
        findViewById(R.id.AboutMeMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeMessage.this,MeMessage_AboutMe.class);
                startActivity(intent);
            }
        });

    }
}
