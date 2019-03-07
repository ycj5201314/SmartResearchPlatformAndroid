package com.example.administrator.partner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Info.UserInfo;
import Interface.RequestFunc;
import Soap.WebService;
import Widget.Back;

/**
 * Created by FengRui on 2016/2/23.
 */
public class MeBrief extends Activity {
    private final String METHOD_GET_INFO = "UserInfo";
    private final String METHOD_SEND_INFO = "UpdateInfo";
    private String UID;
    WebService http_info, http_send;
    UserInfo info = new UserInfo();
    List<EditText> etList = new ArrayList<>();
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_brief);
        createSOAP();
        initControl();
        getData();
        ((Back)findViewById(R.id.btn_back)).setContext(MeBrief.this);//返回按钮
    }

    //创建SOAP
    private void createSOAP() {
        http_info = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_info.Result != null) {
                    formatData(http_info.Result.split("｜"));
                }
            }
        });

        http_send = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_send.Result != null && http_send.Result.equals("true")) {
                    Toast.makeText(MeBrief.this, "已成功更新个人简介!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MeBrief.this, "请重试", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //解析数据
    private void formatData(String[] list) {
        if (list.length < 8) return;
        info.setName(list[0]);
        info.setSex(list[1]);
        info.setSchool(list[2]);
        info.setAge(list[3]);
        info.setLike(list[4]);
        info.setBrief(list[5]);
        info.setQuestion(list[6]);
        info.setAnswer(list[7]);

        showInfo();
    }

    //请求数据
    private void getData() {
        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        UID = sp.getString("UserName", "");

        HashMap<String, String> values = new HashMap<>();
        values.put("UserID", UID);
        http_info.Request(METHOD_GET_INFO, values);
    }

    //显示数据
    private void showInfo() {
        etList.get(0).setText(info.getName());
        if (info.getSex().equals("男"))
            radioGroup.check(R.id.radio_man);
        else if (info.getSex().equals("女"))
            radioGroup.check(R.id.radio_women);
        etList.get(1).setText(info.getSchool());
        etList.get(2).setText(info.getAge());
        etList.get(3).setText(info.getLike());
        etList.get(4).setText(info.getBrief());
        etList.get(5).setText(info.getQuestion());
        etList.get(6).setText(info.getAnswer());

        //去除未填写内容
        LinearLayout ly = (LinearLayout) findViewById(R.id.linearLayout);
        for (int i = 0; i < ly.getChildCount(); i++) {
            View v = ly.getChildAt(i);
            if (v instanceof LinearLayout) {
                LinearLayout cly = (LinearLayout) v;
                for (int j = 0; j < cly.getChildCount(); j++) {
                    View view = cly.getChildAt(j);
                    if (view instanceof EditText) {
                        EditText et = (EditText) view;
                        if (et.getText().toString().equals("未填写"))
                            et.setText("");
                    }
                }
            }
        }
    }

    //提交数据
    private void updateData() {
        HashMap<String, String> values = new HashMap<>();
        if (etList.get(0).getText().toString().length() == 0)
            Toast.makeText(MeBrief.this, "昵称为必填项", Toast.LENGTH_SHORT).show();
        values.put("UserID", UID);
        values.put("Name", etList.get(0).getText().toString());
        values.put("Sex", info.getSex());
        values.put("School", etList.get(1).getText().toString());
        values.put("Age", etList.get(2).getText().toString());
        values.put("Like", etList.get(3).getText().toString());
        values.put("Brief", etList.get(4).getText().toString());
        values.put("Question", etList.get(5).getText().toString());
        values.put("Answer", etList.get(6).getText().toString());
        http_send.Request(METHOD_SEND_INFO, values);
    }

    //初始化控件
    private void initControl() {
        int[] idList = {R.id.et_name, R.id.et_school, R.id.et_age, R.id.et_like, R.id.et_brief, R.id.et_question, R.id.et_anwser};
        for (int id : idList) {
            EditText et = (EditText) findViewById(id);
            etList.add(et);
        }

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_man:
                        info.setSex("男");
                        break;
                    case R.id.radio_women:
                        info.setSex("女");
                        break;
                }
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }

}
