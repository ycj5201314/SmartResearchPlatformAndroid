package com.example.administrator.partner;

import android.app.Activity;
import android.os.Bundle;

import Widget.Back;

/**
 * Created by FengRui on 2016/2/10.
 */
public class MeWealth extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_wealth);
        ((Back)findViewById(R.id.btn_back)).setContext(MeWealth.this);//返回按钮
    }
}
