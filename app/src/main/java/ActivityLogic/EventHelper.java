package ActivityLogic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.administrator.partner.AddProject;
import com.example.administrator.partner.DataDevote;
import com.example.administrator.partner.DataDownload;
import com.example.administrator.partner.DataUpload;
import com.example.administrator.partner.FriendsAdd;
import com.example.administrator.partner.FriendsContact;
import com.example.administrator.partner.MeBrief;
import com.example.administrator.partner.MeDevote;
import com.example.administrator.partner.MeMessage;
import com.example.administrator.partner.MeProject;
import com.example.administrator.partner.MeWealth;
import com.example.administrator.partner.ProjectDetail;
import com.example.administrator.partner.R;
import com.example.administrator.partner.Register_Login;
import com.example.administrator.partner.UserDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Info.UserInfo;
import Interface.RequestFunc;
import Soap.WebService;
import Utils.ImageBase64;
import Utils.WriteRead_Image;
import Widget.AutoListView;
import Widget.AutoListView.OnLoadListener;
import Widget.AutoListView.OnRefreshListener;
import Widget.CircleImageView;


public class EventHelper {
    Button project, data, friends, me;
    FrameLayout fl0, fl1, fl2, fl3;
    Context context;
    Activity activity;
    LayoutInflater inflater;
    public View view;//当前子界面的view

    public EventHelper(Context context) {
        this.context = context;
        activity = (Activity) context;
        BottomClickListener();
        inflater = activity.getLayoutInflater();
        project.performClick();
    }


    /*绑定底栏四按钮监听*/
    public void BottomClickListener() {
        project = (Button) activity.findViewById(R.id.Project);
        data = (Button) activity.findViewById(R.id.Data);
        friends = (Button) activity.findViewById(R.id.Friends);
        me = (Button) activity.findViewById(R.id.Me);

        project.setOnClickListener(new MyClickListener());
        data.setOnClickListener(new MyClickListener());
        friends.setOnClickListener(new MyClickListener());
        me.setOnClickListener(new MyClickListener());

        fl0 = (FrameLayout) activity.findViewById(R.id.fl0);
        fl1 = (FrameLayout) activity.findViewById(R.id.fl1);
        fl2 = (FrameLayout) activity.findViewById(R.id.fl2);
        fl3 = (FrameLayout) activity.findViewById(R.id.fl3);
    }

    /*移除页面控件*/
    private void RemoveView() {
        FrameLayout ly = (FrameLayout) activity.findViewById(R.id.FrameLayout_main);
        ly.removeAllViews();
    }

    /*增加页面控件*/
    private View AddView(int id) {
        View view = inflater.inflate(id, null);
        FrameLayout ly = (FrameLayout) activity.findViewById(R.id.FrameLayout_main);
        ly.addView(view);
        return view;
    }

    /*底栏四按钮监听的事件*/
    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Project:
                    RemoveView();
                    setBackgroundDrawable(R.id.Project);
                    view = AddView(R.layout.home_project);
                    new ProjectBind();
                    break;
                case R.id.Data:
                    RemoveView();
                    setBackgroundDrawable(R.id.Data);
                    view = AddView(R.layout.home_data);
                    new DataBind();
                    break;
                case R.id.Friends:
                    RemoveView();
                    setBackgroundDrawable(R.id.Friends);
                    view = AddView(R.layout.home_friends);
                    new FriendsBind();
                    break;
                case R.id.Me:
                    RemoveView();
                    setBackgroundDrawable(R.id.Me);
                    new MeBind();
                    break;
                default:
                    break;
            }
        }

        //底栏按钮变色
        private void setBackgroundDrawable(int btn) {
            if (btn == R.id.Project) {
                project.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_bule_top));
                fl0.setForeground(context.getResources().getDrawable(R.drawable.project_w));
            } else {
                project.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_gray));
                fl0.setForeground(context.getResources().getDrawable(R.drawable.project));
            }

            if (btn == R.id.Data) {
                data.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_bule_top));
                fl1.setForeground(context.getResources().getDrawable(R.drawable.data_w));

            } else {
                data.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_gray));
                fl1.setForeground(context.getResources().getDrawable(R.drawable.data));

            }

            if (btn == R.id.Friends) {
                friends.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_bule_top));
                fl2.setForeground(context.getResources().getDrawable(R.drawable.friend_w));

            } else {
                friends.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_gray));
                fl2.setForeground(context.getResources().getDrawable(R.drawable.friend));

            }

            if (btn == R.id.Me) {
                me.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_bule_top));
                fl3.setForeground(context.getResources().getDrawable(R.drawable.me_w));

            } else {
                me.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_gray));
                fl3.setForeground(context.getResources().getDrawable(R.drawable.me));
            }

        }
    }

    /*项目页面数据加载*/
    private class ProjectBind implements OnRefreshListener, OnLoadListener {
        private ArrayList<HashMap<String, String>> allList = new ArrayList<>();//ListView的总数据源
        private ArrayList<HashMap<String, String>> newList = new ArrayList<>();//ListView的新数据源
        private List<String> tempList;//接受数据等待转换
        private AutoListView lv;
        private int pageNum = 0;
        private int thisRequest;//记录请求类型
        private boolean Checked;//记录仅众筹状态
        private WebService http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null) {
                    if (!http.Result.equals("null")) {
                        String[] arr = http.Result.split("｜");
                        tempList = Arrays.asList(arr);
                        getData();
                    } else {
                        newList.clear();
                        refreshListView();
                    }
                }
            }
        });

        final String METHOD_PROJECT_LIST = "ReturnProjectList";
        MyAdapter adapter = new MyAdapter(context);

        public ProjectBind() {
            initListView();
            AddProjectBind();
            loadData(AutoListView.REFRESH);
            ControlListener();
        }

        private void initListView() {
            lv = (AutoListView) view.findViewById(R.id.ListView01);
            lv.setAdapter(adapter);
            lv.setOnRefreshListener(this);
            lv.setOnLoadListener(this);
        }

        private void loadData(int what) {
            thisRequest = what;
            // 从服务器获取数据
            Map<String, String> values = new HashMap<>();
            if (what == AutoListView.REFRESH) {
                pageNum = 0;
                values.put("page", "0");
            } else if (what == AutoListView.LOAD) {
                values.put("page", String.valueOf(++pageNum));
            }
            if (Checked)
                values.put("type", "众筹");
            else
                values.put("type", "全部");
            http.Request(METHOD_PROJECT_LIST, values);
        }

        //事件监听
        private void ControlListener() {
            //只看众筹切换
            Switch swh = (Switch) view.findViewById(R.id.switch_raised);
            swh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Checked = isChecked;
                    loadData(AutoListView.REFRESH);
                }
            });
        }

        //规格化数据
        private void getData() {
            newList.clear();
            for (int i = 0; i < tempList.size(); i += 7) {
                HashMap<String, String> tempHashMap = new HashMap<>();
                tempHashMap.put("UserName", tempList.get(i));
                tempHashMap.put("UserID", tempList.get(i + 1));
                tempHashMap.put("ProjectID", tempList.get(i + 2));
                tempHashMap.put("ItemText", tempList.get(i + 3));
                tempHashMap.put("FaceImage", tempList.get(i + 4));
                tempHashMap.put("ProjectName", tempList.get(i + 5));
                tempHashMap.put("RaisedDate", tempList.get(i + 6));
                newList.add(tempHashMap);
            }
            refreshListView();
        }

        //根据触发事件更新ListView
        private void refreshListView() {
            switch (thisRequest) {
                case AutoListView.REFRESH:
                    lv.onRefreshComplete();
                    allList.clear();
                    allList.addAll(newList);
                    break;
                case AutoListView.LOAD:
                    lv.onLoadComplete();
                    allList.addAll(newList);
                    break;
            }
            lv.setResultSize(newList.size());
            adapter.getItemFaceImage();
            adapter.notifyDataSetChanged();
        }

        //发布按钮事件
        public void AddProjectBind() {
            ImageButton IBtn = (ImageButton) view.findViewById(R.id.AddProject);
            IBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddProject.class);
                    activity.startActivity(intent);
                }
            });
        }

        @Override
        public void onRefresh() {
            loadData(AutoListView.REFRESH);
        }

        @Override
        public void onLoad() {
            loadData(AutoListView.LOAD);
        }

        //Item数据绑定,按钮监听
        class MyAdapter extends BaseAdapter {
            private LayoutInflater inflater;
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
                ViewHolder holder;
                if (myView == null) {
                    // 取得要显示的行View
                    myView = inflater.inflate(R.layout.project_listview_item, null, true);
                    holder = new ViewHolder();
                    holder.UserID = (TextView) myView.findViewById(R.id.UserName);
                    holder.ItemText = (TextView) myView.findViewById(R.id.ItemText);
                    holder.RaisedDate = (TextView) myView.findViewById(R.id.tv_raisedDate);
                    holder.ProjectName = (TextView) myView.findViewById(R.id.ProjectName);
                    holder.FaceImage = (CircleImageView) myView.findViewById(R.id.FaceImage);
                    myView.setTag(holder);
                } else {
                    holder = (ViewHolder) myView.getTag();
                    holder.RaisedDate.setText("");
                }


                // 让行View的每个组件与数据源相关联
                holder.UserID.setText(allList.get(pos).get("UserName"));
                holder.ItemText.setText(allList.get(pos).get("ItemText"));
                holder.ProjectName.setText(allList.get(pos).get("ProjectName"));
                if (!allList.get(pos).get("RaisedDate").equals("null"))
                    holder.RaisedDate.setText("众筹至" + allList.get(pos).get("RaisedDate"));
                final String PID = allList.get(pos).get("ProjectID");
                final String UID = allList.get(pos).get("UserID");

                if (imageHash.size() > 0 && imageHash.get(UID) != null)
                    holder.FaceImage.setImageBitmap(imageHash.get(UID));//显示头像

                // 添加事件响应
                holder.FaceImage.setOnClickListener(new View.OnClickListener() {//头像点击
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UserDetail.class);
                        intent.putExtra("UserID", UID);
                        activity.startActivity(intent);
                    }
                });
                holder.ProjectName.setOnClickListener(new View.OnClickListener() {//内容点击
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProjectDetail.class);
                        intent.putExtra("ProjectID", PID);
                        activity.startActivity(intent);
                    }
                });
                holder.ItemText.setOnClickListener(new View.OnClickListener() {//内容点击
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProjectDetail.class);
                        intent.putExtra("ProjectID", PID);
                        activity.startActivity(intent);
                    }
                });
                return myView;
            }

            //ViewHolder重用视图,减少findViewById次数
            private class ViewHolder {
                TextView UserID;
                TextView ItemText;
                TextView RaisedDate;
                TextView ProjectName;
                CircleImageView FaceImage;
            }

            //获取列表头像资源
            public void getItemFaceImage() {
                //不重复的UID集合
                ArrayList<String> IDList = new ArrayList<>();
                String UserIDList = "";
                for (int i = 0; i < newList.size(); i++) {
                    String uid = newList.get(i).get("UserID");
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
                for (HashMap<String, String> itemInfo : newList) {
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

    /*资料页面数据加载*/
    private class DataBind {
        public DataBind() {
            btnBind();
        }

        //按钮监听
        private void btnBind() {
            (view.findViewById(R.id.btn_DataUpload)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DataUpload.class);
                    activity.startActivity(intent);
                }
            });
            (view.findViewById(R.id.btn_DataDownload)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DataDownload.class);
                    activity.startActivity(intent);
                }
            });
            (view.findViewById(R.id.btn_Devote)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DataDevote.class);
                    activity.startActivity(intent);
                }
            });
        }
    }

    /*好友页面数据加载*/
    private class FriendsBind {
        public FriendsBind() {
            btnBind();
        }

        //按钮监听
        private void btnBind() {
            (view.findViewById(R.id.SearchFriend)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FriendsAdd.class);
                    activity.startActivity(intent);
                }
            });
            (view.findViewById(R.id.Friend)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FriendsContact.class);
                    activity.startActivity(intent);
                }
            });
        }
    }

    /*我的页面数据加载*/
    private class MeBind {
        List<String> UserInfoList;
        UserInfo userInfo = new UserInfo();
        CircleImageView iv_face;
        WriteRead_Image wri;
        final String METHOD_USER_INFO = "UserInfo";
        final String METHOD_DOWN_FACE = "DownFaceImage";
        final String METHOD_GET_NOTIFICATION = "GetNotification";
        ListView lv_notification;
        SharedPreferences SP = context.getSharedPreferences("State", Activity.MODE_PRIVATE);
        WebService http_face, http_user, http_noti;
        MyAdapter adapter = new MyAdapter(context);
        private List<String> tempList;//接受数据等待转换
        private ArrayList<HashMap<String, String>> allList = new ArrayList<>();//ListView的总数据源
        HorizontalScrollView horizontalScrollView;

        //初始化
        public MeBind() {
            createSOAP();
            userInfo.setID(SP.getString("UserName", ""));
            //初始化视图
            view = inflater.inflate(R.layout.home_me, null);
            FrameLayout ly = (FrameLayout) activity.findViewById(R.id.FrameLayout_main);
            ly.addView(view);
            horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
            iv_face = (CircleImageView) view.findViewById(R.id.FaceImg);
            //个人信息
            HashMap<String, String> values = new HashMap<>();
            values.put("UserID", userInfo.getID());
            http_user.Request(METHOD_USER_INFO, values);
            //请求通知
            HashMap<String, String> values_noti = new HashMap<>();
            values_noti.put("UserID", userInfo.getID());
            http_noti.Request(METHOD_GET_NOTIFICATION, values_noti);

            BtnBind();
            init_Notification();

        }

        //创建SOAP
        private void createSOAP() {
            http_face = new WebService(new RequestFunc() {
                @Override
                public void Func() {
                    if (http_face.Result != null) {
                        if (!http_face.Result.equals("NoExists")) {
                            Bitmap face = ImageBase64.base64ToBitmap(http_face.Result);
                            iv_face.setImageBitmap(face);//显示头像
                            wri.setBitMap(face);
                            wri.saveBitmap();//缓存到本地
                        }
                    }
                }
            });

            http_user = new WebService(new RequestFunc() {
                @Override
                public void Func() {
                    if (http_user.Result != null) {
                        UserInfoList = CutString(http_user.Result);
                        PutUserInfo();
                        ShowInfo();
                    }
                }
            });

            http_noti = new WebService(new RequestFunc() {
                @Override
                public void Func() {
                    if (http_noti.Result != null) {
                        String[] arr = http_noti.Result.split("｜");
                        tempList = Arrays.asList(arr);
                        getData();
                    }
                }
            });
        }

        //规格化数据
        private void getData() {
            allList.clear();
            for (int i = 0; i < tempList.size(); i += 4) {
                HashMap<String, String> tempHashMap = new HashMap<>();
                tempHashMap.put("NotiContent", tempList.get(i));
                tempHashMap.put("NotiDirec", tempList.get(i + 1));
                tempHashMap.put("Time", tempList.get(i + 2));
                tempHashMap.put("NotiID", tempList.get(i + 3));
                allList.add(tempHashMap);
            }
            adapter.notifyDataSetChanged();
            lv_notification.setVisibility(View.VISIBLE);
        }

        //通知中心
        private void init_Notification() {
            lv_notification = (ListView) view.findViewById(R.id.notification_list);
            lv_notification.setAdapter(adapter);
        }

        //展示信息
        private void ShowInfo() {
            //尝试读取本地图片缓存,不存在则下载
            Map<String, String> values0 = new HashMap<>();
            values0.put("UserID", userInfo.getID());
            wri = new WriteRead_Image("FaceImg/", userInfo.getID());
            if (wri.Exists()) {
                iv_face.setImageBitmap(wri.readBitmap());
            } else {
                http_face.Request(METHOD_DOWN_FACE, values0);
            }

            TextView tx_nickname = (TextView) view.findViewById(R.id.NickName);//用户名称
            TextView tx_Place = (TextView) view.findViewById(R.id.Place);//用户学校
            TextView tx_Like = (TextView) view.findViewById(R.id.Like);//擅长

            if (tx_nickname == null)
                return;
            tx_nickname.setText(userInfo.getName());
            tx_Place.setText(userInfo.getSchool());
            tx_Like.setText(userInfo.getLike());
        }

        //按钮事件
        private void BtnBind() {
            Button btn_Logout = (Button) view.findViewById(R.id.btn_Logout);
            Button btn_MyProject = (Button) view.findViewById(R.id.MyProject);
            Button btn_MyMessage = (Button) view.findViewById(R.id.MyMessage);
            Button btn_MyWealth = (Button) view.findViewById(R.id.MyWealth);
            Button btn_MyBrief = (Button) view.findViewById(R.id.MyBrief);
            Button btn_MyData = (Button) view.findViewById(R.id.MyData);
            btn_MyBrief.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MeBrief.class);
                    context.startActivity(intent);
                }
            });
            btn_Logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logout();
                    Intent intent = new Intent(context, Register_Login.class);
                    context.startActivity(intent);
                    activity.finish();
                }
            });
            btn_MyProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MeProject.class);
                    context.startActivity(intent);
                }
            });
            btn_MyMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MeMessage.class);
                    context.startActivity(intent);
                }
            });
            btn_MyWealth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MeWealth.class);
                    context.startActivity(intent);
                }
            });
            btn_MyData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MeDevote.class);
                    context.startActivity(intent);
                }
            });
            iv_face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setType("image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 150);
                    intent.putExtra("outputY", 150);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra("noFaceDetection", true);
                    activity.startActivityForResult(intent, 1);
                }

            });
        }

        //登出账户
        private void Logout() {
            SharedPreferences.Editor editor = SP.edit();
            editor.clear();
            editor.commit();
        }

        //剪切字符串
        private List<String> CutString(String s) {
            String arr[] = s.split("｜");
            List<String> list = Arrays.asList(arr);
            return list;
        }

        //初始化UserInfo对象
        private void PutUserInfo() {
            userInfo.setName(UserInfoList.get(0));
            userInfo.setSchool(UserInfoList.get(2));
            userInfo.setLike(UserInfoList.get(4));
        }

        //通知listView适配
        private class MyAdapter extends BaseAdapter {
            private LayoutInflater inflater;
            final String METHOD_READ_OVER = "SetNofication";

            WebService http_notNotifi = new WebService(new RequestFunc() {
                @Override
                public void Func() {

                }
            });

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
                // 取得要显示的行View
                myView = inflater.inflate(R.layout.me_notification_item, null);
                TextView title = (TextView) myView.findViewById(R.id.noti_title);
                TextView content = (TextView) myView.findViewById(R.id.noti_content);
                TextView time = (TextView) myView.findViewById(R.id.noti_time);
                Button delete = (Button) myView.findViewById(R.id.del);

                // 让行View的每个组件与数据源相关联
                title.setText(allList.get(pos).get("NotiDirec"));
                content.setText(allList.get(pos).get("NotiContent"));
                time.setText(allList.get(pos).get("Time"));

                if (allList.get(pos).get("NotiDirec").equals("无通知")) {
                    delete.setVisibility(View.INVISIBLE);
                }
                final String ID = allList.get(pos).get("NotiID");
                // 添加事件响应
                delete.setOnClickListener(new View.OnClickListener() {//头像点击
                    @Override
                    public void onClick(View v) {
                        allList.remove(pos);
                        adapter.notifyDataSetChanged();
                        HashMap<String, String> values = new HashMap<>();
                        values.put("ID", ID);
                        http_notNotifi.Request(METHOD_READ_OVER, values);
                    }
                });
                return myView;
            }

        }
    }

}
