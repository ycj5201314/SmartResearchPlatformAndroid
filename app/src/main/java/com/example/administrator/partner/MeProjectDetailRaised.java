package com.example.administrator.partner;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.HashMap;

import Interface.RequestFunc;
import PopWindows.MeProjectDetailRaisedCalender;
import Soap.WebService;
import Utils.ImageBase64;
import Widget.Back;
import Widget.DynamicScrollView;

/**
 * Created by FengRui on 2016/2/10.
 */
public class MeProjectDetailRaised extends Activity {
    DynamicScrollView scrollView;
    LinearLayout imgLy;
    TextView tv_ProjectName;
    EditText et_Target, et_Brief;
    Button btn_SelectCalendar, btn_AddRow, btn_SendRaised, chooseImg;
    LinearLayout ly;
    int Year, Month, Day;
    Bitmap bitmap;
    WebService http;
    String PID;
    final String METHOD_SEND_RAISED = "SendRaised";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_project_detail_raised);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//禁止弹出键盘
        initControl();
        getIntentExtra();
        ControlListener();
        CreateSOAP();
        ((Back)findViewById(R.id.btn_back)).setContext(MeProjectDetailRaised.this);//返回按钮
    }

    //获取传值
    private void getIntentExtra() {
        tv_ProjectName.setText(getIntent().getStringExtra("ProjectName"));
        PID = getIntent().getStringExtra("ProjectID");
    }

    //创建SOAP
    private void CreateSOAP() {
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null && http.Result.equals("true")) {
                    Toast.makeText(MeProjectDetailRaised.this, "发布众筹成功", Toast.LENGTH_SHORT).show();
                    MeProjectDetailRaised.this.finish();
                } else {
                    Toast.makeText(MeProjectDetailRaised.this, "发布失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //初始化控件
    private void initControl() {
        scrollView = (DynamicScrollView) findViewById(R.id.scrollView);
        imgLy = (LinearLayout) findViewById(R.id.imgLy);
        tv_ProjectName = (TextView) findViewById(R.id.tv_ProjectName);
        et_Target = (EditText) findViewById(R.id.et_target);
        btn_SelectCalendar = (Button) findViewById(R.id.btn_selectCalendar);
        et_Brief = (EditText) findViewById(R.id.et_brief);
        chooseImg = (Button) findViewById(R.id.img_brief);
        btn_SendRaised = (Button) findViewById(R.id.btn_sendRaised);
        ly = (LinearLayout) findViewById(R.id.linearLayout);
    }

    //监听
    private void ControlListener() {
        //选择日期
        btn_SelectCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                MeProjectDetailRaisedCalender.CallBack callBack = new MeProjectDetailRaisedCalender.CallBack() {
                    @Override
                    public void CallBackRespond(int year, int month, int day) {
                        //回传的日期
                        Year = year;
                        Month = month;
                        Day = day;
                        btn_SelectCalendar.setText(Year + "-" + (Month + 1) + "-" + Day + "");
                    }
                };
                MeProjectDetailRaisedCalender pop = new MeProjectDetailRaisedCalender(MeProjectDetailRaised.this, callBack);
                pop.showAtLocation(pop.view.findViewById(R.id.pop), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        //添加图片
        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 1);
            }
        });

        //添加一档
        btn_AddRow = (Button) findViewById(R.id.btn_addRow);
        btn_AddRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout vly = new LinearLayout(MeProjectDetailRaised.this);
                EditText et_money = new EditText(MeProjectDetailRaised.this);
                EditText et_repay = new EditText(MeProjectDetailRaised.this);
                Drawable drawable = getResources().getDrawable(R.drawable.edittext_bg);
                //设置背景
                et_money.setBackgroundDrawable(drawable);
                et_repay.setBackgroundDrawable(drawable);
                //设置布局
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
                params.setMargins(5, 5, 0, 0);
                params.height = 80;
                et_money.setLayoutParams(params);
                et_repay.setLayoutParams(params);
                //居中显示文字
                et_money.setGravity(Gravity.CENTER);
                et_repay.setGravity(Gravity.CENTER);
                //输入类型
                et_money.setInputType(InputType.TYPE_CLASS_NUMBER);
                //单行显示
                et_money.setSingleLine(true);
                et_repay.setSingleLine(true);
                //限制长度
                et_money.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                et_repay.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
                //显示View
                vly.addView(et_money);
                vly.addView(et_repay);
                ly.addView(vly);
            }
        });

        //发布
        btn_SendRaised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String target, calendar, brief, img = null, repayList = "";
                target = et_Target.getText().toString();
                calendar = btn_SelectCalendar.getText().toString();
                //检查日期是否已选择
                if (calendar.equals("选择日期")) {
                    Toast.makeText(MeProjectDetailRaised.this, "请选择日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                brief = et_Brief.getText().toString();

                //遍历editText
                for (int i = 0; i < ly.getChildCount(); i++) {
                    LinearLayout childLy = (LinearLayout) ly.getChildAt(i);
                    for (int k = 0; k < childLy.getChildCount(); k++) {
                        View view = childLy.getChildAt(k);
                        if (view instanceof EditText) {
                            EditText et = (EditText) view;
                            String txt = et.getText().toString();
                            if (txt.length() == 0) {//如果未填写则返回
                                Toast.makeText(MeProjectDetailRaised.this, "请完整填写支持与回报", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            repayList += txt + "|";
                        }
                    }
                }
                //检查填写是否完整
                if (target.equals("") || calendar.equals("") || brief.equals("")) {
                    Toast.makeText(MeProjectDetailRaised.this, "请填写所有空缺", Toast.LENGTH_SHORT).show();
                    return;
                }
                //图转Base64
                if (bitmap != null) {
                    img = ImageBase64.bitmapToBase64(bitmap, 80);
                }
                //发送数据
                Toast.makeText(MeProjectDetailRaised.this, "请稍候...", Toast.LENGTH_LONG).show();
                HashMap<String, String> values = new HashMap<>();
                values.put("ProjectID", PID);
                values.put("Target", target);
                values.put("Calendar", calendar);
                values.put("Brief", brief);
                if (img != null)
                    values.put("Img", img);
                else
                    values.put("Img", "null");
                values.put("RepayList", repayList);
                http.Request(METHOD_SEND_RAISED, values);
            }
        });
    }

    //选图片回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                imgLy.removeAllViews();
                scrollView.initResource(bitmap, imgLy, MeProjectDetailRaised.this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
