package com.example.administrator.partner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Info.ProjectInfo;
import Interface.RequestFunc;
import Soap.WebService;
import Widget.Back;
import Widget.FavoriteCheckBox;

/**
 * Created by Administrator on 2016/1/8.
 */
public class MeProjectDetail extends Activity {
    ProjectInfo PI = new ProjectInfo();
    List<String> JoinList = new ArrayList<>();
    ArrayList<TextView> tvList = new ArrayList<>();//上半部控件
    Button btn_ThinkJoin, btn_JoinRaised;//申请按钮
    ListView lv;//下部列表
    WebService http_GetProInfo;
    String uid;
    String raised;

    final String METHOD_PROJECT_DETAIL = "ReturnNowProjectDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_project_detail);
        CreateHttpObject();
        Init();
        ((Back) findViewById(R.id.btn_back)).setContext(MeProjectDetail.this);//返回按钮
    }

    //创建Soap对象并传入被重写方法的请求对象
    private void CreateHttpObject() {
        http_GetProInfo = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_GetProInfo.Result != null) {
                    String[] arr = http_GetProInfo.Result.split("｜");
                    List<String> tempList = Arrays.asList(arr);
                    GetAllString(tempList);
                }
            }
        });
    }

    //执行必要方法
    private void Init() {
        Intent intent = getIntent();
        PI.setProjectID(intent.getStringExtra("ProjectID"));

        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        uid = sp.getString("UserName", "");

        Map<String, String> values = new HashMap<>();
        values.put("ProjectID", PI.getProjectID());
        values.put("UserID", uid);

        http_GetProInfo.Request(METHOD_PROJECT_DETAIL, values);

        new FavoriteCheckBox(this, uid, PI);//收藏按钮处理类
    }

    //初始化控件
    private void Control_init() {
        int[] id_List = {R.id.tv_ProjectName, R.id.tv_DutyPeople, R.id.tv_Teacher, R.id.tv_Content, R.id.tv_ProjectType, R.id.tv_ProjectState,//上半部分id
                R.id.btn_thinkJoin, R.id.btn_JoinRaised,//申请按钮,众筹id
                R.id.listView_Join};//下部列表id

        for (int i = 0; i < 6; i++) {
            tvList.add((TextView) findViewById(id_List[i]));
        }
        btn_ThinkJoin = (Button) findViewById(id_List[6]);
        btn_JoinRaised = (Button) findViewById(id_List[7]);
        lv = (ListView) findViewById(id_List[8]);
        //申请按钮绑定界面跳转
        btn_ThinkJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeProjectDetail.this, ProjectDetail.class);
                intent.putExtra("ProjectID", PI.getProjectID());
                MeProjectDetail.this.startActivity(intent);
            }
        });

    }

    //解析字符串
    private void GetAllString(List<String> list) {
        if (list.size() == 9) {
            int i = 0;
            PI.setProjectName(list.get(i++)); //项目名称
            PI.setPic(list.get(i++));//用户名称
            PI.setTeacher(list.get(i++));//指导老师
            PI.setProjectContent(list.get(i++));//项目内容
            PI.setState(list.get(i++));//项目状态
            PI.setProjectType(list.get(i++));//项目类型
            raised = list.get(i++); //能否众筹
            PI.setJoinPeople(list.get(i++));//参与人员
            PI.setWhetherJoin(list.get(i));//是否参加
            //判断参与人员非空后解析字符串
            if (!PI.getJoinPeople().equals("暂无")) {
                JoinList = CutJoinPeopleString(PI.getJoinPeople());
            } else {
                JoinList.add("尚未有人参加");
            }
            SetInfo();
        }
    }

    //剪切参与人员字符串
    private List<String> CutJoinPeopleString(String s) {
        String arr[] = s.split("\\|");
        List<String> aList = Arrays.asList(arr);// 职责:xxx
        return aList;
    }

    //展示信息
    private void SetInfo() {
        Control_init();

        if (raised.equals("可众筹")) {
            //众筹按钮跳转
            btn_JoinRaised.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeProjectDetail.this, MeProjectDetailRaised.class);
                    intent.putExtra("ProjectID", PI.getProjectID());
                    intent.putExtra("ProjectName", PI.getProjectName());
                    MeProjectDetail.this.startActivity(intent);
                }
            });
        } else if (raised.equals("已众筹")) {
            btn_JoinRaised.setText("查看众筹详情");
            btn_JoinRaised.setVisibility(View.VISIBLE);
            btn_JoinRaised.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeProjectDetail.this, ProjectDetail.class);
                    intent.putExtra("ProjectID", PI.getProjectID());
                    startActivity(intent);
                }
            });
        } else {
            btn_JoinRaised.setVisibility(View.GONE);
        }

        tvList.get(0).setText(PI.getProjectName());
        tvList.get(1).setText(PI.getPic());
        tvList.get(2).setText(PI.getTeacher());
        tvList.get(3).setText(PI.getProjectContent());
        tvList.get(4).setText(PI.getProjectType());
        tvList.get(5).setText(PI.getState());
        if (!PI.getState().equals("完成") && PI.getWhetherJoin().equals("未参加")) {
            findViewById(R.id.btn_thinkJoin).setVisibility(View.VISIBLE);
        }
        lv.setAdapter(new MyAdapter(this));
    }

    /*Item数据绑定,按钮监听*/
    public class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public MyAdapter(Context c) {
            this.inflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return JoinList.size();
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
            myView = inflater.inflate(R.layout.me_project_join_item, null);
            TextView tv_JoinPeople = (TextView) myView.findViewById(R.id.tv_JoinPeople);
            tv_JoinPeople.setText(JoinList.get(pos));
            return myView;
        }
    }
}
