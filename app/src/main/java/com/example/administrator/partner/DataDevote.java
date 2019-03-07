package com.example.administrator.partner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Interface.RequestFunc;
import Soap.WebService;
import Widget.Back;

/**
 * Created by Administrator on 2016/1/27.
 */
public class DataDevote extends Activity {
    private ArrayList<HashMap<String, String>> allList = new ArrayList<>();//ListView的总数据源
    private List<String> tempList;//接受数据等待转换
    private WebService http, http_Search;
    final String METHOD_DEVOTE_SEARCH = "ReturnWordDevoteList";
    final String METHOD_RETURN_DEVOTE_LIST = "ReturnDevoteList";
    EditText et_Search;
    ListView lv;
    MyAdapter adapter;
    String[] label;
    String[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_devote);
        initControl();
        NoShowKeyBoard();
        initListView();
        createSOAP();
        loadData();
        ((Back)findViewById(R.id.btn_back)).setContext(DataDevote.this);//返回按钮
    }

    //请求数据
    private void loadData() {
        label = getResources().getStringArray(R.array.Devote_Label);
        colors = getResources().getStringArray(R.array.Devote_Label_Color);

        http.Request(METHOD_RETURN_DEVOTE_LIST);
    }

    //创建SOAP
    private void createSOAP() {
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null) {
                    String[] arr = http.Result.split("｜");
                    tempList = Arrays.asList(arr);
                    getData(0);
                }
            }
        });
        http_Search = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_Search.Result != null) {
                    if (!http_Search.Result.equals("null")) {
                        String[] arr = http_Search.Result.split("｜");
                        tempList = Arrays.asList(arr);
                        getData(1);
                    } else {
                        Toast.makeText(DataDevote.this, "没有匹配的结果", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //初始化控件
    private void initControl() {
        et_Search = (EditText) findViewById(R.id.devote_Search);
        lv = (ListView) findViewById(R.id.listView);
    }

    //规格化数据并刷新listView
    private void getData(int type) {
        allList.clear();
        for (int i = 0, j = 0; i < tempList.size(); i += 3) {
            HashMap<String, String> tempHashMap = new HashMap<>();
            //用户账号
            tempHashMap.put("UserID", tempList.get(i));
            //用户名称
            tempHashMap.put("UserName", tempList.get(i + 1));
            //资料贡献
            tempHashMap.put("DataNum", tempList.get(i + 2));
            //颜色,贡献排名,称号
            if (type == 0) {
                tempHashMap.put("Color", colors[j]);
                tempHashMap.put("Ranking", String.valueOf(j + 1));
                tempHashMap.put("Label", label[j++]);
            } else {
                int index = Integer.parseInt(tempList.get(i++ + 3));
                tempHashMap.put("Ranking", String.valueOf(index));
                //是否大于10名
                if (index < 11) {
                    tempHashMap.put("Label", label[index - 1]);
                    tempHashMap.put("Color", colors[index - 1]);
                } else {
                    tempHashMap.put("Label", "平民");
                    tempHashMap.put("Color", "#888888");
                }
            }
            allList.add(tempHashMap);
        }
        adapter.notifyDataSetChanged();
    }

    //禁止弹出键盘,回车搜索,键盘隐藏
    private void NoShowKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        et_Search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    HashMap<String, String> values = new HashMap<>();
                    String word = et_Search.getText().toString();
                    if (word.length() > 0) {
                        values.put("Word", word);
                        http_Search.Request(METHOD_DEVOTE_SEARCH, values);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                    } else
                        http.Request(METHOD_RETURN_DEVOTE_LIST);
                }
                return false;
            }
        });
    }

    //适配listView数据源
    private void initListView() {
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);
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
            myView = inflater.inflate(R.layout.data_devote_item, null);
            TextView Ranking = (TextView) myView.findViewById(R.id.tv_ranking);
            TextView UserName = (TextView) myView.findViewById(R.id.tv_username);
            TextView Label = (TextView) myView.findViewById(R.id.tv_label);
            TextView DataNum = (TextView) myView.findViewById(R.id.tv_dataNum);
            TextView LabelHead = (TextView) myView.findViewById(R.id.tv_laHead);

            // 让行View的每个组件与数据源相关联
            Ranking.setText("Rank:" + allList.get(pos).get("Ranking"));
            UserName.setText(allList.get(pos).get("UserName"));
            Label.setText(allList.get(pos).get("Label"));
            DataNum.setText("上传" + allList.get(pos).get("DataNum"));
            //设置Item颜色
            int color = Color.parseColor(allList.get(pos).get("Color"));
            Ranking.setTextColor(color);
            UserName.setTextColor(color);
            Label.setTextColor(color);
            LabelHead.setTextColor(color);

            final String UID = allList.get(pos).get("UserID");
            // 添加事件响应
            UserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DataDevote.this, UserDetail.class);
                    intent.putExtra("UserID", UID);
                    startActivity(intent);
                }
            });
            return myView;
        }
    }
}
