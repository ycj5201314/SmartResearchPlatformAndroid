package com.example.administrator.partner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Interface.RequestFunc;
import Soap.WebService;
import Widget.AutoListView;
import Widget.AutoListView.OnLoadListener;
import Widget.AutoListView.OnRefreshListener;
import Widget.Back;

/**
 * Created by FengRui on 2016/2/25.
 */
public class MeDevote extends Activity implements OnRefreshListener, OnLoadListener {

    private ArrayList<HashMap<String, String>> allList = new ArrayList<>();//ListView的总数据源
    private ArrayList<HashMap<String, String>> newList = new ArrayList<>();//ListView的新数据源
    private List<String> tempList;//接受数据等待转换
    MyAdapter adapter;
    private AutoListView lv;
    private int pageNum = 0;
    private int thisRequest;//记录请求类型
    final String METHOD_DATA_LIST = "ReturnMyDataList";
    final String METHOD_DATA_SEARCH = "ReturnMyDataSearch";
    final String METHOD_DATA_DELETE = "DeleteData";
    private WebService http, http_Search, http_Delete;
    EditText search;
    boolean AllowLoad = true;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_devote);
        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        UID = sp.getString("UserName", "");
        NoShowKeyBoard();
        initSOAP();
        initListView();
        loadData(AutoListView.REFRESH);
        ((Back) findViewById(R.id.btn_back)).setContext(MeDevote.this);//返回按钮
    }

    //初始化SOAP对象
    private void initSOAP() {
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null && !http.Result.equals("null")) {
                    String[] arr = http.Result.split("｜");
                    tempList = Arrays.asList(arr);
                    getData();
                } else {
                    Toast.makeText(MeDevote.this, "你还没有上传过资\n,快去上传一份吧!", Toast.LENGTH_LONG).show();
                }
            }
        });
        http_Search = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_Search.Result != null && !http_Search.Result.equals("null")) {
                    String[] arr = http_Search.Result.split("｜");
                    tempList = Arrays.asList(arr);
                    thisRequest = AutoListView.COVER;//请求类型变更
                    getData();
                } else {
                    Toast.makeText(MeDevote.this, "没有匹配的结果,请重新提炼关键词", Toast.LENGTH_SHORT).show();
                }
            }
        });
        http_Delete = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_Delete.Result != null && !http_Delete.Result.equals("false")) {
                    loadData(AutoListView.REFRESH);
                    Toast.makeText(MeDevote.this, "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MeDevote.this, "请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //禁止弹出键盘,回车搜索,键盘隐藏
    private void NoShowKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        search = (EditText) findViewById(R.id.et_Search);
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    HashMap<String, String> values = new HashMap<>();
                    String word = search.getText().toString();
                    if (word.length() > 0) {
                        values.put("UserID", UID);
                        values.put("Word", word);
                        http_Search.Request(METHOD_DATA_SEARCH, values);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                    }
                }
                return false;
            }
        });
    }

    //初始化listView
    private void initListView() {
        adapter = new MyAdapter(this);
        lv = (AutoListView) findViewById(R.id.ListView01);
        lv.setAdapter(adapter);
        lv.setOnRefreshListener(this);
        lv.setOnLoadListener(this);
    }

    //规格化数据
    private void getData() {

        newList.clear();
        for (int i = 0; i < tempList.size(); i += 4) {
            HashMap<String, String> tempHashMap = new HashMap<>();
            tempHashMap.put("DataID", tempList.get(i));
            tempHashMap.put("DataName", tempList.get(i + 1));
            tempHashMap.put("DataBrief", tempList.get(i + 2));
            tempHashMap.put("SeeNum", tempList.get(i + 3));
            newList.add(tempHashMap);
        }
        refreshListView();
    }

    //根据触发事件更新ListView
    private void refreshListView() {
        switch (thisRequest) {
            case AutoListView.REFRESH:
                AllowLoad = true;
                lv.onRefreshComplete();
                allList.clear();
                allList.addAll(newList);
                break;
            case AutoListView.LOAD:
                lv.onLoadComplete();
                allList.addAll(newList);
                break;
            case AutoListView.COVER:
                AllowLoad = false;
                allList.clear();
                allList.addAll(newList);
                break;
        }
        lv.setResultSize(newList.size());
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
        public View getView(final int pos, View myView, ViewGroup parent) {
            ViewHolder holder;
            if (myView == null) {
                // 取得要显示的行View
                myView = inflater.inflate(R.layout.me_devote_item, null);
                holder = new ViewHolder();
                holder.DataName = (TextView) myView.findViewById(R.id.tv_name);
                holder.DataBrief = (TextView) myView.findViewById(R.id.tv_brief);
                holder.SeeNum = (TextView) myView.findViewById(R.id.tv_seeNum);
                holder.Down = (Button) myView.findViewById(R.id.btn_download);
                holder.Del = (Button) myView.findViewById(R.id.del);
                myView.setTag(holder);
            } else {
                holder = (ViewHolder) myView.getTag();
            }

            final String Name = allList.get(pos).get("DataName");

            // 让行View的每个组件与数据源相关联
            holder.DataName.setText(Name);
            holder.DataBrief.setText(allList.get(pos).get("DataBrief"));
            holder.SeeNum.setText("浏览" + Integer.parseInt(allList.get(pos).get("SeeNum")) + "次");

            //详情
            final String DataID = allList.get(pos).get("DataID");
            holder.Down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeDevote.this, DataDownloadDetail.class);
                    intent.putExtra("DataID", DataID);
                    startActivity(intent);
                }
            });
            //删除
            holder.Del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MeDevote.this);

                    builder.setTitle("确定要永久删除 [" + Name + "] 吗?\n这将无法恢复");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            HashMap<String, String> values = new HashMap<>();
                            values.put("DataID", DataID);
                            http_Delete.Request(METHOD_DATA_DELETE, values);
                        }
                    });
                    builder.setNegativeButton("不删了", null);
                    builder.create().show();
                }
            });
            return myView;
        }

        //ViewHolder重用视图,减少findViewById次数
        private class ViewHolder {
            TextView DataName;
            TextView DataBrief;
            TextView SeeNum;
            Button Down;
            Button Del;
        }

    }

    //从服务器获取数据
    private void loadData(int what) {
        thisRequest = what;
        Map<String, String> values = new HashMap<>();
        values.put("UserID", UID);
        if (what == AutoListView.REFRESH) {
            pageNum = 0;
            values.put("Page", "0");
        } else if (what == AutoListView.LOAD && AllowLoad) {
            values.put("pageNum", String.valueOf(++pageNum));
        }
        if (values.size() > 0)
            http.Request(METHOD_DATA_LIST, values);
    }

    @Override
    public void onRefresh() {
        loadData(AutoListView.REFRESH);
    }

    @Override
    public void onLoad() {
        loadData(AutoListView.LOAD);
    }
}
