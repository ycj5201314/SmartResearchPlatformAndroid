package com.example.administrator.partner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Interface.RequestFunc;
import Soap.WebService;
import Utils.ImageBase64;
import Utils.WriteRead_Image;
import Widget.AutoListView;
import Widget.AutoListView.OnLoadListener;
import Widget.AutoListView.OnRefreshListener;
import Widget.CircleImageView;

/**
 * Created by Administrator on 2016/1/16.
 */
public class MeMessage_AboutMe extends Activity implements OnRefreshListener, OnLoadListener {
    private ArrayList<HashMap<String, String>> ProjectList = new ArrayList<>();//项目相关ListView的数据源
    private ArrayList<HashMap<String, String>> MoodList = new ArrayList<>();//心情相关ListView的数据源
    final String METHOD_MESSAGE_ABOUT_ME_PROJECT = "ReturnProjectMessageList";
    final String METHOD_MESSAGE_ABOUT_ME_MOOD = "ReturnMoodMessageList";
    private AutoListView alv;
    private ListView lv;
    private int thisRequest;//记录请求类型
    MyAdapter adapter;
    MyAdapter adapter0;
    private WebService http, http0;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_message_aboutme);
        adapter = new MyAdapter(MeMessage_AboutMe.this, ProjectList);
        UserID = getSharedPreferences("State", Activity.MODE_PRIVATE).getString("UserName", "");
        CreateHttpObject();
        SetLayout();
        initData();
    }

    //选项卡初始化
    private void SetLayout() {
        TabHost th = (TabHost) findViewById(R.id.tabHost);
        th.setup(); //初始化TabHost容器
        th.addTab(th.newTabSpec("tab1").setIndicator("项目相关", null).setContent(R.id.linearLayout));  //在TabHost创建标签，然后设置：标题／图标／标签页布局
        th.addTab(th.newTabSpec("tab2").setIndicator("心情相关", null).setContent(R.id.linearLayout1));
    }

    //创建SOAP对象并重写
    private void CreateHttpObject() {
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null) {
                    String[] arr = http.Result.split("｜");
                    List<String> tempList = Arrays.asList(arr);
                    ProjectList.clear();
                    ProjectList.addAll(getData(tempList));
                    adapter.notifyDataSetChanged();
                    adapter.getItemFaceImage();
                }
            }
        });
        http0 = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http0.Result != null) {

                }
            }
        });
    }

    //规格化数据
    private ArrayList<HashMap<String, String>> getData(List<String> list) {
        ArrayList<HashMap<String, String>> hashMapArrayList = new ArrayList<>();
        if (list.size() >= 6) {
            for (int i = 0; i < list.size(); i += 6) {
                HashMap<String, String> tempHashMap = new HashMap<>();
                tempHashMap.put("ProjectName", list.get(i));
                tempHashMap.put("Duty", list.get(i + 1));
                tempHashMap.put("UserName", list.get(i + 2));
                tempHashMap.put("Face", list.get(i + 3));
                tempHashMap.put("UserID", list.get(i + 4));
                tempHashMap.put("PID", list.get(i + 5));
                hashMapArrayList.add(tempHashMap);
            }
        }
        return hashMapArrayList;
    }

    //初始化listView请求数据
    public void initData() {
        lv = (ListView) findViewById(R.id.ListView);
        lv.setAdapter(adapter);
        Map<String, String> values = new HashMap<>();
        values.put("UserID", UserID);

        http.Request(METHOD_MESSAGE_ABOUT_ME_PROJECT, values);
    }

    @Override
    public void onLoad() {
        loadData(AutoListView.REFRESH);
    }

    @Override
    public void onRefresh() {
        loadData(AutoListView.LOAD);
    }

    private void loadData(int what) {
        thisRequest = what;
        // 从服务器获取数据
        Map<String, String> values = new HashMap<>();
        values.put("UserID", UserID);

        http.Request(METHOD_MESSAGE_ABOUT_ME_MOOD, values);
    }

    /*Item数据绑定,按钮监听*/
    public class MyAdapter extends BaseAdapter {
        Context context;
        Activity activity;
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, String>> List;
        final String METHOD_UPDATE_PROJECT_APPLY = "UpdateProjectApply";
        final String METHOD_DOWN_FACE_LIST = "DownFaceImageList";
        HashMap<String, Bitmap> imageHash = new HashMap<>();
        WebService http_faceList = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_faceList.Result != null) {
                    saveItemFaceImage(http_faceList.Result.split("｜"));
                }
            }
        });
        WebService http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null) {
                    if (http.Result.equals("true")) {
                        Toast.makeText(context, "处理成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "请重试", Toast.LENGTH_SHORT).show();
                    }
                    initData();
                }
            }
        });

        public MyAdapter(Context c, ArrayList<HashMap<String, String>> List) {
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
            ViewHolder holder;
            if (myView == null) {
                // 取得要显示的行View
                myView = inflater.inflate(R.layout.me_message_aboutme_project_item, null);
                holder = new ViewHolder();
                holder.ProjectName = (TextView) myView.findViewById(R.id.projectname);
                holder.UserName = (TextView) myView.findViewById(R.id.username);
                holder.Duty = (TextView) myView.findViewById(R.id.duty);
                holder.FaceImg = (CircleImageView) myView.findViewById(R.id.faceimg);
                holder.Agree = (Button) myView.findViewById(R.id.btn_agree);
                holder.Reject = (Button) myView.findViewById(R.id.btn_reject);
                myView.setTag(holder);
            } else {
                holder = (ViewHolder) myView.getTag();
            }

            final String UID =  List.get(pos).get("UserID");
            String PID =  List.get(pos).get("PID");
            String PN = List.get(pos).get("ProjectName");
            String DUTY =  List.get(pos).get("Duty");

            // 让行View的每个组件与数据源相关联
            holder.ProjectName.setText(List.get(pos).get("ProjectName"));
            holder.UserName.setText( List.get(pos).get("UserName"));
            holder.Duty.setText(List.get(pos).get("Duty"));

            if (imageHash.size() > 0 && imageHash.get(UID) != null)
                holder.FaceImg.setImageBitmap(imageHash.get(UID));//显示头像

            final HashMap<String, String> values = new HashMap<>();
            values.put("UserID", UID);
            values.put("ProjectID", PID);
            values.put("Duty", DUTY);
            values.put("ProjectName", PN);

            // 用户名点击进入用户简介
            holder.UserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeMessage_AboutMe.this, UserDetail.class);
                    intent.putExtra("UserID", UID);
                    startActivity(intent);
                }
            });
            holder.FaceImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeMessage_AboutMe.this, UserDetail.class);
                    intent.putExtra("UserID", UID);
                    startActivity(intent);
                }
            });
            //同意按钮
            holder.Agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    values.put("Intent", "Agree");
                    http.Request(METHOD_UPDATE_PROJECT_APPLY, values);
                }
            });
            //拒绝按钮
            holder.Reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    values.put("Intent", "Reject");
                    http.Request(METHOD_UPDATE_PROJECT_APPLY, values);
                }
            });
            return myView;
        }

        //ViewHolder重用视图,减少findViewById次数
        private class ViewHolder {
            TextView ProjectName;
            TextView UserName;
            TextView Duty;
            CircleImageView FaceImg;
            Button Agree;
            Button Reject;
        }

        //获取列表头像资源
        public void getItemFaceImage() {
            //不重复的UID集合
            ArrayList<String> IDList = new ArrayList<>();
            String UserIDList = "";
            for (int i = 0; i < ProjectList.size(); i++) {
                String uid = ProjectList.get(i).get("UserID");
                WriteRead_Image wri = new WriteRead_Image("FaceImg/", uid);
                if (!wri.Exists()) {
                    if (!IDList.contains(uid)) {
                        IDList.add(uid);
                    }
                }
            }
            for (String id : IDList) {
                UserIDList += id + ",";
            }

            //如果有未缓存的头像则请求下载,否则直接读取本地缓存
            if (UserIDList.length() > 0) {
                HashMap<String, String> values = new HashMap<>();
                values.put("UserIDList", UserIDList);
                http_faceList.Request(METHOD_DOWN_FACE_LIST, values);
            } else {
                showItemFaceImage();
                adapter.notifyDataSetChanged();
            }
        }

        //存储头像资源
        private void saveItemFaceImage(String[] baseArr) {
            //解析字符串
            HashMap<String, String> base64 = new HashMap<>();//UserID:Base64
            for (String s : baseArr) {
                String[] temp = s.split(":");
                if (temp.length >= 2)
                    base64.put(temp[0], temp[1]);
            }
            //遍历HashMap
            Iterator<Map.Entry<String, String>> iterator = base64.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String uid = entry.getKey();
                String baseCode = entry.getValue();
                if (!baseCode.equals("null")) {
                    //缓存图片
                    Bitmap bitmap = ImageBase64.base64ToBitmap(baseCode);
                    WriteRead_Image wri = new WriteRead_Image("FaceImg/", uid);
                    wri.setBitMap(bitmap);
                    wri.saveBitmap();
                }
            }
            showItemFaceImage();
            adapter.notifyDataSetChanged();
        }

        //读取Item头像资源到内存
        private void showItemFaceImage() {
            for (HashMap<String, String> itemInfo : ProjectList) {
                final String UID = itemInfo.get("UserID");
                if (!imageHash.containsKey(UID)) {
                    WriteRead_Image wri = new WriteRead_Image("FaceImg/", UID);
                    if (wri.Exists())
                        imageHash.put(UID, wri.readBitmap());
                }
            }
        }
    }
}
