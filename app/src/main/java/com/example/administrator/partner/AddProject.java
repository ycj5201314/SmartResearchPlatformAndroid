package com.example.administrator.partner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import Interface.RequestFunc;
import Soap.WebService;
import Widget.Back;

/**
 * Created by Administrator on 2015/12/15.
 */
public class AddProject extends Activity {
    String UserName, ProjectName, ProjectContent, ProjectTeacher, NeedPeople, ProjectType;
    WebService http = new WebService(new RequestFunc() {
        @Override
        public void Func() {
            if (http.Result != null) {
                if (http.Result.equals("true")) {
                    Toast.makeText(AddProject.this, "发布成功", Toast.LENGTH_SHORT).show();
                    AddProject.this.finish();
                } else {
                    Toast.makeText(AddProject.this, "发布失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
    final String METHOD_INSERT_PROJECT = "InsertProject";
    private int ROWS = 0;
    int[] idList_tv = {R.id.Duty1, R.id.Duty2, R.id.Duty3, R.id.Duty4,};
    int[] idList_sp = {R.id.Project_NeedNum1, R.id.Project_NeedNum2, R.id.Project_NeedNum3, R.id.Project_NeedNum4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_project_add_project);
        AddBtnBind();
        AddRow();
        ((Back)findViewById(R.id.btn_back)).setContext(AddProject.this);//返回按钮
    }

    private void AddBtnBind() {
        Button addBtn = (Button) findViewById(R.id.btn_AddProject);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetProjectString();
            }
        });
    }

    private void AddRow() {
        Button addBtn = (Button) findViewById(R.id.btn_AddRow);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ROWS < 4) {
                    (findViewById(idList_tv[ROWS])).setVisibility(View.VISIBLE);
                    (findViewById(idList_sp[ROWS])).setVisibility(View.VISIBLE);
                    ROWS++;
                    if (ROWS == 4)
                        (findViewById(R.id.btn_AddRow)).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void SetProjectString() {
        SharedPreferences SP = getSharedPreferences("State", Activity.MODE_PRIVATE);
        UserName = SP.getString("UserName", "");//用户账户

        EditText ProjectName_et = (EditText) findViewById(R.id.Project_Name);
        EditText ProjectContent_et = (EditText) findViewById(R.id.Project_Content);
        EditText ProjectTeacher_et = (EditText) findViewById(R.id.Project_Teacher);
        Spinner ProjectType_sp = (Spinner) findViewById(R.id.sp_ProjectType);

        ProjectName = ProjectName_et.getText().toString();//项目名
        ProjectContent = ProjectContent_et.getText().toString();//项目内容
        ProjectTeacher = ProjectTeacher_et.getText().toString();//指导老师
        ProjectType = ProjectType_sp.getSelectedItem().toString();//项目类型

        for (int i = 0; i < 5; i++) {
            NeedPeople = getNeedString();//需求人员类型及数量
        }

        if (!ProjectName.equals("") && !ProjectContent.equals("")) {
            Map<String, String> values = new HashMap<>();
            values.put("UserName", UserName);
            values.put("ProjectName", ProjectName);
            values.put("ProjectContent", ProjectContent);
            values.put("ProjectTeacher", ProjectTeacher);
            values.put("NeedPeople", NeedPeople);
            values.put("ProjectType", ProjectType);
            http.Request(METHOD_INSERT_PROJECT, values);
        } else {
            Toast.makeText(AddProject.this, "请填写必要信息:名称,内容,职责", Toast.LENGTH_SHORT).show();
        }
    }

    /*拼接 需求人员 信息并返回*/
    private String getNeedString() {
        String Str = "";

        int[] Duty_id = {R.id.Duty0, R.id.Duty1, R.id.Duty2, R.id.Duty3, R.id.Duty4};
        int[] Num_id = {R.id.Project_NeedNum0, R.id.Project_NeedNum1, R.id.Project_NeedNum2, R.id.Project_NeedNum3, R.id.Project_NeedNum4};

        for (int i = 0; i < 5; i++) {
            EditText Duty = (EditText) findViewById(Duty_id[i]);
            Spinner NeedNum = (Spinner) findViewById(Num_id[i]);

            String duty_str = Duty.getText().toString();
            String NeedNum_str = NeedNum.getSelectedItem().toString();

            if (!duty_str.equals("")) {
                Str += duty_str + ":" + NeedNum_str + "|";
            }
        }
        return Str;
    }
}
