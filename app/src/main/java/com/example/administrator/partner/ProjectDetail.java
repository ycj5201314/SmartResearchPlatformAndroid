package com.example.administrator.partner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Info.ProjectInfo;
import Info.RaisedInfo;
import Interface.RequestFunc;
import Soap.WebService;
import Utils.ImageBase64;
import Widget.Back;
import Widget.DynamicScrollView;
import Widget.FavoriteCheckBox;

/**
 * Created by Administrator on 2015/11/13.
 */
public class ProjectDetail extends Activity implements View.OnClickListener {
    String uid;
    ProjectInfo PI = new ProjectInfo();
    RaisedInfo RI = new RaisedInfo();
    ArrayList<TextView> tvList = new ArrayList<>();
    ArrayList<Button> btnList = new ArrayList<>();
    ArrayList<LinearLayout> lyList = new ArrayList<>();
    WebService http_Apply, http_GetProInfo, http_GetRaised, http_Surpport;
    TextView tv_Target, tv_RaisedDate, tv_Brief, tv_HadMoney, tv_ImgTitle;
    LinearLayout ly_support, ly_img;
    View raisedView;
    DynamicScrollView scrollView;
    final String METHOD_PROJECT_DETAIL = "ReturnProjectDetail";
    final String METHOD_APPLY_JOIN = "UpdateApplyJoin";
    final String METHOD_RAISED = "ReturnRaised";
    final String METHOD_SUPPORT = "UpdateHadMoney";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_detail);
        CreateHttpObject();
        Init();
        ((Back) findViewById(R.id.btn_back)).setContext(ProjectDetail.this);//返回按钮
    }

    //创建Soap对象并传入被重写方法的请求对象
    private void CreateHttpObject() {
        http_Apply = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_Apply.Result.equals("true")) {
                    Toast.makeText(ProjectDetail.this, "申请成功", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < 5; i++) {
                        btnList.get(i).setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ProjectDetail.this, "请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        http_GetProInfo = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_GetProInfo.Result != null) {
                    String[] arr = http_GetProInfo.Result.split("｜");
                    GetAllString(Arrays.asList(arr));
                }
            }
        });

        http_GetRaised = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_GetRaised.Result != null) {
                    String[] arr = http_GetRaised.Result.split("｜");
                    GetRaisedString(Arrays.asList(arr));
                    btnList.get(5).setVisibility(View.GONE);//隐藏加载按钮
                }
            }
        });

        http_Surpport = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_Surpport.Result != null && http_Surpport.Result.equals("true")) {
                    Toast.makeText(ProjectDetail.this, "已成功支持该项目的众筹!\n当众筹金额达到目标时\n将提醒您使用现金正式支持!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(ProjectDetail.this, "请重试", Toast.LENGTH_LONG).show();
            }
        });
    }

    //执行必要方法
    private void Init() {
        PI.setProjectID(getIntent().getStringExtra("ProjectID"));

        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        uid = sp.getString("UserName", "");

        Map<String, String> values = new HashMap<>();
        values.put("ProjectID", PI.getProjectID());
        values.put("UserID", uid);

        http_GetProInfo.Request(METHOD_PROJECT_DETAIL, values);

        new FavoriteCheckBox(this, uid, PI);//收藏按钮处理类
    }

    //众筹字符串解析
    private void GetRaisedString(List<String> list) {
        if (list.size() == 6) {
            for (int i = 0; i < list.size(); ) {
                RI.setTarget(list.get(i++));//目标金额
                RI.setDateTime(list.get(i++));//预设日期
                RI.setBrief(list.get(i++));//文字简介
                RI.setSupportAndRepay(CutSupportAndRepay(list.get(i++)));//支持与回报
                RI.setHadMoney(list.get(i++));//已筹资金
                RI.setImgBrief(ImageBase64.base64ToBitmap(list.get(i++)));//图片简介
            }
        }
        raisedViewInit();
    }

    //解析字符串
    private void GetAllString(List<String> list) {
        if (list.size() == 9) {
            for (int i = 0; i < list.size(); ) {
                PI.setProjectName(list.get(i++)); //项目名称
                PI.setPic(list.get(i++));//用户名称
                PI.setTeacher(list.get(i++));//指导老师
                PI.setProjectContent(list.get(i++));//项目内容
                PI.setNeedPeople(list.get(i++));//需求人员
                PI.setProjectType(list.get(i++));//项目类型
                PI.setState(list.get(i++));//项目状态
                PI.setWhetherJoin(list.get(i++));//是否参与
                PI.setRaisedDate(list.get(i++));//众筹时间
            }
            SetInfo();
            ApplyBtnBind();
        }
    }

    //加载众筹信息
    private void SetRaisedInfo() {
        tv_Target.setText(RI.getTarget() + "￥");
        tv_RaisedDate.setText(RI.getDateTime());
        tv_Brief.setText(RI.getBrief());
        tv_HadMoney.setText(RI.getHadMoney());
        //遍历HashMap
        Iterator<Map.Entry<String, String>> iterator = RI.getSupportAndRepay().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            final String Support = entry.getKey();//支持金额
            String Repay = entry.getValue();//回报方式
            //创建视图
            LayoutParams params0 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0);
            params0.setMargins(0, 5, 0, 5);
            params0.gravity = Gravity.CENTER;
            LinearLayout hly = new LinearLayout(ProjectDetail.this);
            hly.setLayoutParams(params0);
            //支持按钮
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0);
            params.setMargins(0, 10, 0, 10);
            params.height = 80;
            params.gravity = Gravity.CENTER;
            Button btn = new Button(ProjectDetail.this);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProjectDetail.this);

                    builder.setTitle("确定要支持该项目" + Support + "￥吗?\n你将需要在众筹满足条件时进行支付");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            HashMap<String, String> values = new HashMap<>();
                            values.put("ProjectID", PI.getProjectID());
                            values.put("UserID", uid);
                            values.put("Number", Support);
                            http_Surpport.Request(METHOD_SUPPORT, values);
                        }
                    });
                    builder.setNegativeButton("再想想", null);
                    builder.create().show();
                }
            });
            btn.setLayoutParams(params);
            btn.setText("支持" + Support + "￥");
            btn.setTextColor(getResources().getColor(R.color.white));
            btn.setBackground(getResources().getDrawable(R.drawable.ripple));
            //回报方式
            LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 5);
            params1.setMargins(5, 0, 0, 0);
            params1.gravity = Gravity.CENTER;
            TextView tv = new TextView(ProjectDetail.this);
            tv.setMinimumHeight(80);
            tv.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
            tv.setText(Repay);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(params1);
            //显示视图
            hly.addView(btn);
            hly.addView(tv);
            ly_support.addView(hly);
        }
        ((LinearLayout) findViewById(R.id.raisedViewPanel)).addView(raisedView);
        //图片介绍
        if (RI.getImgBrief() == null)
            tv_ImgTitle.setVisibility(View.GONE);
        scrollView.initResource(RI.getImgBrief(), ly_img, ProjectDetail.this);
    }

    //展示信息
    private void SetInfo() {

        Control_init();
        tvList.get(0).setText(PI.getProjectName());
        tvList.get(1).setText(PI.getPic());
        tvList.get(2).setText(PI.getTeacher());
        tvList.get(3).setText(PI.getProjectContent());
        tvList.get(4).setText(PI.getProjectType());
        tvList.get(5).setText(PI.getState());

        //显示控件
        if (!PI.getRaisedDate().equals("null"))
            btnList.get(5).setVisibility(View.VISIBLE);

        List<String> DutyList = CutNeedPeopleString(PI.getNeedPeople());
        for (int i = 0, j = 6; i < DutyList.size(); i++, j++) {
            int index = DutyList.get(i).indexOf(":");
            tvList.get(j).setText(DutyList.get(i).substring(0, index));//职责
            tvList.get(j + 5).setText(DutyList.get(i).substring(index + 1) + " 人");//人数
            tvList.get(j).setVisibility(View.VISIBLE);
            tvList.get(j + 5).setVisibility(View.VISIBLE);
        }
        if (PI.getWhetherJoin().equals("未参加") && !PI.getState().equals("完成")) {
            for (int i = 0; i < DutyList.size(); i++) {
                btnList.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    //初始化众筹视图
    private void raisedViewInit() {
        //获取view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        raisedView = inflater.inflate(R.layout.project_detail_raised, null);
        //获取控件
        scrollView = (DynamicScrollView) findViewById(R.id.scrollView);
        tv_Target = (TextView) raisedView.findViewById(R.id.tv_target);
        tv_RaisedDate = (TextView) raisedView.findViewById(R.id.tv_raisedDate);
        tv_HadMoney = (TextView) raisedView.findViewById(R.id.tv_hadMoney);
        tv_Brief = (TextView) raisedView.findViewById(R.id.tv_brief);
        tv_ImgTitle = (TextView) raisedView.findViewById(R.id.ImgTitle);
        ly_support = (LinearLayout) raisedView.findViewById(R.id.supportAndRepay);
        ly_img = (LinearLayout) raisedView.findViewById(R.id.imgLy);
        SetRaisedInfo();
    }

    //初始化控件
    private void Control_init() {
        int[] id_List = {R.id.tv_ProjectName, R.id.tv_DutyPeople, R.id.tv_Teacher, R.id.tv_Content, R.id.tv_ProjectType, R.id.tv_ProjectState,//上部分ID集0~5
                R.id.tv_Duty0, R.id.tv_Duty1, R.id.tv_Duty2, R.id.tv_Duty3, R.id.tv_Duty4,//职责名称ID集6~10
                R.id.tv_NeedNum0, R.id.tv_NeedNum1, R.id.tv_NeedNum2, R.id.tv_NeedNum3, R.id.tv_NeedNum4,//需求人数ID集11~15
                R.id.btn_Join0, R.id.btn_Join1, R.id.btn_Join2, R.id.btn_Join3, R.id.btn_Join4,//申请ID集16~20
                R.id.btn_loadRaised, //加载众筹21
                R.id.raisedViewPanel, R.id.imgLy};//示图容器22,23

        for (int i = 0; i < 16; i++) {
            tvList.add((TextView) findViewById(id_List[i]));
        }
        for (int i = 16; i < 22; i++) {
            btnList.add((Button) findViewById(id_List[i]));
        }
        for (int i = 22; i < 24; i++) {
            lyList.add((LinearLayout) findViewById(id_List[i]));
        }
    }

    //申请 按钮事件监听
    private void ApplyBtnBind() {
        for (int i = 0; i < 6; i++) {
            btnList.get(i).setOnClickListener(this);
        }
    }

    //剪切支持与回报字符串
    private HashMap<String, String> CutSupportAndRepay(String s) {
        String arr[] = s.split("\\|");
        HashMap<String, String> SR = new HashMap<>();
        for (int i = 0; i < arr.length; ) {
            SR.put(arr[i++], arr[i++]);//支持金额:回报方式
        }
        return SR;
    }

    //剪切需求人员字符串
    private List<String> CutNeedPeopleString(String s) {
        String arr[] = s.split("\\|");
        List<String> aList = Arrays.asList(arr);// 职责:2
        return aList;
    }

    //申请按钮监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Join0:
                ApplyJoin(0);
                break;
            case R.id.btn_Join1:
                ApplyJoin(1);
                break;
            case R.id.btn_Join2:
                ApplyJoin(2);
                break;
            case R.id.btn_Join3:
                ApplyJoin(3);
                break;
            case R.id.btn_Join4:
                ApplyJoin(4);
                break;
            case R.id.btn_loadRaised:
                loadRaised();
                break;
        }
    }

    //加载众筹部分
    private void loadRaised() {
        //请求数据
        HashMap<String, String> values = new HashMap<>();
        values.put("ProjectID", PI.getProjectID());
        http_GetRaised.Request(METHOD_RAISED, values);
    }

    //申请按钮事件会调用此方法
    private void ApplyJoin(int i) {
        List<String> list = CutNeedPeopleString(PI.getNeedPeople());
        String s = list.get(i);
        String duty = s.substring(0, s.indexOf(":"));//职责

        SharedPreferences SP = getSharedPreferences("State", Activity.MODE_PRIVATE);
        String UserID = SP.getString("UserName", "");

        Map<String, String> values = new HashMap<>();
        values.put("UserID", UserID);//当前用户账号
        values.put("ProjectID", PI.getProjectID());//项目编号
        values.put("Duty", duty);//所申请的职责

        http_Apply.Request(METHOD_APPLY_JOIN, values);
    }


}
