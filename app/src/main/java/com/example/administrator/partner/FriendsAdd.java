package com.example.administrator.partner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Interface.RequestFunc;
import PopWindows.DataDownloadDetailComment;
import PopWindows.FriendsAddGroup;
import Soap.WebService;
import Utils.ImageBase64;
import Widget.Back;

/**
 * Created by FengRui on 2016/2/18.
 */
public class FriendsAdd extends Activity {
    EditText search;
    ListView lv;
    MyAdapter adapter;
    String UID;
    private ArrayList<HashMap<String, String>> allList = new ArrayList<>();//ListView的总数据源
    private List<String> tempList;//接受数据等待转换
    private WebService http_add, http_Search;
    final String METHOD_USER_LIST = "SearchUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_add);
        NoShowKeyBoard();
        initListView();
        initSOAP();
        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        UID = sp.getString("UserName", "");
        ((Back) findViewById(R.id.btn_back)).setContext(FriendsAdd.this);//返回按钮
    }

    //回车搜索,键盘隐藏
    private void NoShowKeyBoard() {

        search = (EditText) findViewById(R.id.et_Search);
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    HashMap<String, String> values = new HashMap<>();
                    String word = search.getText().toString();
                    if (word.length() > 0) {
                        values.put("Word", word);
                        http_Search.Request(METHOD_USER_LIST, values);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                    }
                }
                return false;
            }
        });
    }

    //初始化SOAP对象
    private void initSOAP() {
        http_Search = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_Search.Result != null && !http_Search.Result.equals("null")) {
                    String[] arr = http_Search.Result.split("｜");
                    tempList = Arrays.asList(arr);
                    getData();
                } else {
                    Toast.makeText(FriendsAdd.this, "没有匹配的结果,请重新提炼关键词", Toast.LENGTH_SHORT).show();
                }
            }
        });
        http_add = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_add.Result != null && !http_add.Result.equals("false")) {
                    Toast.makeText(FriendsAdd.this, "添加成功!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //初始化listView
    private void initListView() {
        adapter = new MyAdapter(this);
        lv = (ListView) findViewById(R.id.ListView);
        lv.setAdapter(adapter);
    }

    //规格化数据
    private void getData() {
        allList.clear();
        for (int i = 0; i < tempList.size(); i += 4) {
            HashMap<String, String> tempHashMap = new HashMap<>();
            tempHashMap.put("UserName", tempList.get(i));//用户名称
            tempHashMap.put("School", tempList.get(i + 1));//学校
            tempHashMap.put("UserID", tempList.get(i + 2));//用户账号
            tempHashMap.put("FaceBase", tempList.get(i + 3));//头像base64
            allList.add(tempHashMap);
        }
        adapter.notifyDataSetChanged();
    }

    //Item数据绑定,按钮监听
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context c) {
            this.inflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return allList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int pos, View myView, ViewGroup parent) {
            // 取得要显示的行View
            myView = inflater.inflate(R.layout.friends_add_item, null);
            ImageView ImgFace = (ImageView) myView.findViewById(R.id.img_face);
            TextView Name = (TextView) myView.findViewById(R.id.tv_name);
            TextView School = (TextView) myView.findViewById(R.id.tv_school);
            Button Add = (Button) myView.findViewById(R.id.btn_add);

            // 让行View的每个组件与数据源相关联
            ImgFace.setImageBitmap(ImageBase64.base64ToBitmap(allList.get(pos).get("FaceBase")));
            Name.setText(allList.get(pos).get("UserName"));
            School.setText(allList.get(pos).get("School"));

            //事件
            final String YouID = allList.get(pos).get("UserID");
            ImgFace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FriendsAdd.this, UserDetail.class);
                    intent.putExtra("UserID", YouID);
                    startActivity(intent);
                }
            });

            Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendsAddGroup pop = new FriendsAddGroup(FriendsAdd.this, UID, YouID);//初始化对象
                    pop.showAtLocation(pop.view.findViewById(R.id.pop), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                }
            });
            return myView;
        }
    }
}
