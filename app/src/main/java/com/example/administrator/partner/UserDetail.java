package com.example.administrator.partner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import Info.UserInfo;
import Interface.RequestFunc;
import PopWindows.FriendsAddGroup;
import Soap.WebService;
import Utils.ImageBase64;
import Utils.WriteRead_Image;
import Widget.Back;
import Widget.CircleImageView;

/**
 * Created by Administrator on 2015/11/9.
 */
public class UserDetail extends Activity {
    UserInfo info = new UserInfo();
    WebService http_face, http_info;
    Button add;
    CircleImageView face;
    TextView name, sex, age, like, selfBrief, school;
    private String YouID, MyID;
    private final String METHOD_GET_INFO = "UserInfo";
    private final String METHOD_GET_FACE = "DownFaceImage";
    WriteRead_Image wri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdetail);
        createSOAP();
        initControl();
        getData();
        bindBtn();
        ((Back) findViewById(R.id.btn_back)).setContext(UserDetail.this);//返回按钮
    }

    //创建SOAP
    private void createSOAP() {
        http_face = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_face.Result != null && !http_face.Result.equals("NoExists")) {
                    Bitmap bitmap = ImageBase64.base64ToBitmap(http_info.Result);
                    face.setImageBitmap(bitmap);
                    wri.setBitMap(bitmap);
                    wri.saveBitmap();
                }
            }
        });

        http_info = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_info.Result != null && !http_info.Result.equals("null")) {
                    formatData(http_info.Result.split("｜"));
                }
            }
        });
    }

    //初始化控件
    private void initControl() {
        add = (Button) findViewById(R.id.btn_addFriend);
        face = (CircleImageView) findViewById(R.id.face);
        name = (TextView) findViewById(R.id.tv_name);
        sex = (TextView) findViewById(R.id.tv_sex);
        school = (TextView) findViewById(R.id.tv_school);
        age = (TextView) findViewById(R.id.tv_age);
        like = (TextView) findViewById(R.id.tv_like);
        selfBrief = (TextView) findViewById(R.id.tv_selfBrief);
    }

    //获取数据
    private void getData() {
        Intent intent = getIntent();
        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        YouID = intent.getStringExtra("UserID");
        MyID = sp.getString("UserName", "");

        HashMap<String, String> values = new HashMap<>();
        values.put("UserID", YouID);
        http_info.Request(METHOD_GET_INFO, values);

        wri = new WriteRead_Image("FaceImg/", YouID);
        if (wri.Exists())
            face.setImageBitmap(wri.readBitmap());
        else {
            http_face.Request(METHOD_GET_FACE, values);
        }
    }

    //解析数据
    private void formatData(String[] list) {
        info.setName(list[0]);
        info.setSex(list[1]);
        info.setSchool(list[2]);
        info.setAge(list[3]);
        info.setLike(list[4]);
        info.setBrief(list[5]);
        showInfo();
    }

    //展示数据
    private void showInfo() {
        name.setText(info.getName());
        sex.setText(info.getSex());
        school.setText(info.getSchool());
        like.setText(info.getLike());
        age.setText(info.getAge());
        selfBrief.setText(info.getBrief());
    }

    //按钮事件
    private void bindBtn() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendsAddGroup pop = new FriendsAddGroup(UserDetail.this, MyID, YouID);//初始化对象
                pop.showAtLocation(pop.view.findViewById(R.id.pop), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });
    }
}
