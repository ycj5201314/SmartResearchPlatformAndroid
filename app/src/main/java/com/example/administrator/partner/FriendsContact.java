package com.example.administrator.partner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Interface.RequestFunc;
import Soap.WebService;
import Utils.ImageBase64;
import Utils.WriteRead_Image;
import Widget.CircleImageView;
import Widget.ViewPagerAdapter;

/**
 * Created by FengRui on 2016/2/18.
 */
public class FriendsContact extends Activity {

    private ViewPager viewPager;
    private ImageView imageView;
    private List<View> lists = new ArrayList<>();
    private ViewPagerAdapter adapter;
    private MyAdapter myAdapter = new MyAdapter();
    private int screenW;
    private String UID;
    private Animation animation;
    private TextView tab1;
    private TextView tab2;
    List<String> UIDList = new ArrayList<>();//用来加载头像
    List<String> parentNameList = new ArrayList<>();//未展开项名
    HashMap<String, List<HashMap<String, String>>> exList = new HashMap<>();//展开项
    WebService http_friendList;
    private final String METHOD_GET_FRIENDS = "GetFriendList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_contact);
        createSOAP();
        getData();
    }

    //请求数据
    private void getData() {
        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        UID = sp.getString("UserName", "");

        HashMap<String, String> values = new HashMap<>();
        values.put("UserID", UID);
        http_friendList.Request(METHOD_GET_FRIENDS, values);
    }

    //创建SOAP对象
    private void createSOAP() {
        http_friendList = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_friendList != null && !http_friendList.Result.equals("null")) {
                    getData(http_friendList.Result.split("｜"));
                    initControl();
                }
            }
        });
    }

    //初始化控件
    private void initControl() {
        imageView = (ImageView) findViewById(R.id.cursor);
        tab1 = (TextView) findViewById(R.id.textView1);
        tab2 = (TextView) findViewById(R.id.textView2);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        lists.add(getLayoutInflater().inflate(R.layout.friends_contact_message, null));//消息页面
        lists.add(getContactView());//联系人页面
        adapter = new ViewPagerAdapter(lists);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // 当滑动式，顶部的imageView是通过animation缓慢的滑动
            @Override
            public void onPageSelected(int arg0) {
                switch (arg0) {
                    case 0:
                        animation = new TranslateAnimation(screenW / 2, 0, 0, 0);
                        break;
                    case 1:
                        animation = new TranslateAnimation(0, screenW / 2, 0, 0);
                        break;
                }
                animation.setDuration(150); // 光标滑动速度
                animation.setFillAfter(true);
                imageView.startAnimation(animation);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                viewPager.setCurrentItem(0);
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                viewPager.setCurrentItem(1);
            }
        });
        initCursor();
    }

    //初始化联系人页面
    private View getContactView() {
        View view = getLayoutInflater().inflate(R.layout.friends_contact_list, null);
        ExpandableListView exListView = (ExpandableListView) view.findViewById(R.id.exListView);
        exListView.setAdapter(myAdapter);
        return view;
    }

    //规格化数据
    private void getData(String[] list) {
        String groupName = list[2];
        parentNameList.add(groupName);
        ArrayList<HashMap<String, String>> tempList = new ArrayList<>();
        for (int i = 0; i < list.length; i += 3) {
            HashMap<String, String> tempHash = new HashMap<>();
            tempHash.put("好友账号", list[i]);
            tempHash.put("用户名称", list[i + 1]);
            UIDList.add(list[i]);
            if (groupName.equals(list[i + 2])) {
                tempList.add(tempHash);
            } else {
                exList.put(groupName, tempList);
                groupName = list[i + 2];
                parentNameList.add(groupName);
                tempList = new ArrayList<>();
                tempList.add(tempHash);
            }
        }
        exList.put(groupName, tempList);
        myAdapter.getItemFaceImage();
    }

    class MyAdapter extends BaseExpandableListAdapter {

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


        //得到子item需要关联的数据
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String key = parentNameList.get(groupPosition);
            return (exList.get(key).get(childPosition));
        }

        //设置子item的组件
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friends_contact_list_pitem_citem, null);

            String key = parentNameList.get(groupPosition);
            final String UserID = exList.get(key).get(childPosition).get("好友账号");
            final String UserName = exList.get(key).get(childPosition).get("用户名称");

            CircleImageView face = (CircleImageView) convertView.findViewById(R.id.img_face);
            TextView Name = (TextView) convertView.findViewById(R.id.tv_UserName);

            Name.setText(UserName);


            if (imageHash.size() > 0 && imageHash.get(UserID) != null)
                face.setImageBitmap(imageHash.get(UserID));//显示头像

            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FriendsContact.this, UserDetail.class);
                    intent.putExtra("UserID", UserID);
                    startActivity(intent);
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FriendsContact.this, FriendsContactChat.class);
                    intent.putExtra("YouName", UserName);
                    intent.putExtra("YouID", UserID);
                    intent.putExtra("MyID",UID);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        //设置父item组件
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.friends_contact_list_pitem, null);
            }
            TextView GroupName = (TextView) convertView.findViewById(R.id.tv_groupName);
            TextView Number = (TextView) convertView.findViewById(R.id.tv_groupnum);

            Number.setText(exList.get(parentNameList.get(groupPosition)).size() + "");
            GroupName.setText(parentNameList.get(groupPosition));
            return convertView;
        }

        //得到子item的ID
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            String key = parentNameList.get(groupPosition);
            int size = exList.get(key).size();
            return size;
        }

        //获取当前父item的数据
        @Override
        public Object getGroup(int groupPosition) {
            return parentNameList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return parentNameList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        //获取列表头像资源
        public void getItemFaceImage() {
            //不重复的UID集合
            String UserIDList = "";
            for (int i = 0; i < UIDList.size(); i++) {
                String uid = UIDList.get(i);
                WriteRead_Image wri = new WriteRead_Image("FaceImg/", uid);
                if (!wri.Exists()) {
                    if (!UIDList.contains(uid)) {
                        UIDList.add(uid);
                    }
                }
            }

            for (String id : UIDList) {
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
            myAdapter.notifyDataSetChanged();
        }

        //读取Item头像资源到内存
        private void showItemFaceImage() {
            for (int i = 0; i < UIDList.size(); i++) {
                final String UID = UIDList.get(i);
                if (!imageHash.containsKey(UID)) {
                    WriteRead_Image wri = new WriteRead_Image("FaceImg/", UID);
                    if (wri.Exists())
                        imageHash.put(UID, wri.readBitmap());
                }
            }
        }

    }

    //设置滚动图层大小
    private void initCursor() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenW = dm.widthPixels;
        ViewGroup.LayoutParams para = imageView.getLayoutParams();
        para.width = screenW / 2;
        imageView.setLayoutParams(para);
        imageView.setAlpha(70);
    }
}
