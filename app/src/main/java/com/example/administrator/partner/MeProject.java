package com.example.administrator.partner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Interface.RequestFunc;
import Soap.WebService;

/**
 * Created by Administrator on 2016/1/8.
 */
public class MeProject extends Activity {
    private ArrayList<HashMap<String, Object>> JoinList = new ArrayList<>();//ListView的数据源
    private ArrayList<HashMap<String, Object>> LikeList = new ArrayList<>();//ListView的数据源
    final String METHOD_ME_PROJECT_LIST = "ReturnMeProjectList";
    final String METHOD_ME_LIKE_PROJECT_LIST = "ReturnMeLikeProjectList";
    String UserID;

    MyAdapter adapter, adapter0;
    private WebService http, http0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_project);
        CreateHttpObject();
        UserID = getSharedPreferences("State", Activity.MODE_PRIVATE).getString("UserName", "");
        SetLayout();
        initData();
        initSpinner();
    }

    //创建SOAP对象并重写
    private void CreateHttpObject() {
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null) {
                    String[] arr = http.Result.split("｜");
                    List<String> tempList = Arrays.asList(arr);
                    JoinList.clear();
                    JoinList.addAll(getData(tempList));
                    adapter.notifyDataSetChanged();
                }
            }
        });
        http0 = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http0.Result != null) {
                    String[] arr = http0.Result.split("｜");
                    List<String> tempList = Arrays.asList(arr);
                    LikeList.clear();
                    LikeList.addAll(getData(tempList));
                    adapter0.notifyDataSetChanged();
                }
            }
        });
    }

    //选项卡初始化
    private void SetLayout() {
        TabHost th = (TabHost) findViewById(R.id.tabHost);
        th.setup(); //初始化TabHost容器
        th.addTab(th.newTabSpec("tab1").setIndicator("参与的项目", null).setContent(R.id.linearLayout));  //在TabHost创建标签，然后设置：标题／图标／标签页布局
        th.addTab(th.newTabSpec("tab2").setIndicator("收藏的项目", null).setContent(R.id.linearLayout1));
    }

    //下拉控件事件监听
    private void initSpinner() {
        Spinner spinner0 = (Spinner) findViewById(R.id.spinner0);
        spinner0.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                JoinList.clear();
                Map<String, String> values = new HashMap<>();
                values.put("UserID", UserID);
                if (pos == 0) {
                    values.put("Who", "自己");
                } else {
                    values.put("Who", "参与");
                }
                http.Request(METHOD_ME_PROJECT_LIST, values);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                LikeList.clear();
                Map<String, String> values = new HashMap<>();
                values.put("UserID", UserID);
                if (pos == 0) {
                    values.put("State", "进行");
                } else {
                    values.put("State", "完成");
                }
                http0.Request(METHOD_ME_LIKE_PROJECT_LIST, values);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    //请求数据并适配ListView
    private void initData() {
        adapter = new MyAdapter(MeProject.this, JoinList);
        ((ListView) findViewById(R.id.listView)).setAdapter(adapter);
        adapter0 = new MyAdapter(MeProject.this, LikeList);
        ((ListView) findViewById(R.id.listView0)).setAdapter(adapter0);

        Map<String, String> values = new HashMap<>();
        values.put("UserID", UserID);
        values.put("Who", "自己");

        Map<String, String> values0 = new HashMap<>();
        values0.put("UserID", UserID);
        values0.put("State", "进行");

        http.Request(METHOD_ME_PROJECT_LIST, values);
        http0.Request(METHOD_ME_LIKE_PROJECT_LIST, values0);
    }

    //规格化数据
    private ArrayList<HashMap<String, Object>> getData(List<String> list) {
        ArrayList<HashMap<String, Object>> hashMapArrayList = new ArrayList<>();
        if (list.size() >= 3) {
            for (int i = 0; i < list.size(); i += 3) {
                HashMap<String, Object> tempHashMap = new HashMap<>();
                tempHashMap.put("ProjectID", list.get(i));
                tempHashMap.put("ProjectName", list.get(i + 1));
                tempHashMap.put("ProjectContent", list.get(i + 2));
                hashMapArrayList.add(tempHashMap);
            }
        }
        return hashMapArrayList;
    }


    /*Item数据绑定,按钮监听*/
    public class MyAdapter extends BaseAdapter {
        Context context;
        Activity activity;
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, Object>> List;

        public MyAdapter(Context c, ArrayList<HashMap<String, Object>> List) {
            this.List = List;
            context = c;
            activity = (Activity) context;
            this.inflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return List.size();
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
            myView = inflater.inflate(R.layout.me_project_item, null);
            TextView ProjectName = (TextView) myView.findViewById(R.id.ProjectName);
            TextView ProjectContent = (TextView) myView.findViewById(R.id.ProjectContent);
            // 让行View的每个组件与数据源相关联
            ProjectName.setText((String) List.get(pos).get("ProjectName"));
            ProjectContent.setText((String) List.get(pos).get("ProjectContent"));

            // 添加事件响应
            final String PID = (String) List.get(pos).get("ProjectID");
            if (!PID.equals(" "))
                myView.setOnClickListener(new View.OnClickListener() {//内容点击
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MeProjectDetail.class);
                        intent.putExtra("ProjectID", PID);
                        activity.startActivity(intent);
                    }
                });
            return myView;
        }
    }
}
