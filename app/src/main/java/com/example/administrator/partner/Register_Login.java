package com.example.administrator.partner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import Interface.RequestFunc;
import Soap.WebService;

/**
 * Created by Administrator on 2015/12/10.
 */
public class Register_Login extends Activity {
    private Button RegBtn;
    private Button LoginBtn;
    private EditText UserNameEt;
    private EditText PassWordEt;
    private EditText NickNameEt;
    private EditText LoginID;
    private EditText LoginPassword;


    final String METHOD_INSERT = "InsertUser";
    final String METHOD_SELECT = "CheckOne";
    private String UserName, PassWord, NickName;
    private WebService http, http_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_login);
        CreateSOAP();
        initEt();
        CheckLoginState();
        SetLayout();
        initBtn();

    }

    private void CreateSOAP() {
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null) {
                    if (http.Result.equals("true")) {
                        Toast.makeText(Register_Login.this, "注册成功", Toast.LENGTH_SHORT).show();
                        //记录登陆状态并关闭本活动
                        RememberState(UserName, PassWord);
                        StartMain();
                    } else if (http.Result.equals("false")) {
                        Toast.makeText(Register_Login.this, "请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Register_Login.this, "请检查网络状态", Toast.LENGTH_SHORT).show();
                }
            }
        });

        http_login = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_login.Result != null) {
                    if (http_login.Result.equals("true")) {
                        RememberState(UserName, PassWord);
                        StartMain();
                        Toast.makeText(Register_Login.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Register_Login.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Register_Login.this, "请检查网络状态", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SetLayout() {
        TabHost th = (TabHost) findViewById(R.id.tabHost);
        th.setup(); //初始化TabHost容器
        th.addTab(th.newTabSpec("tab1").setIndicator("登陆", null).setContent(R.id.linearLayout1));//在TabHost创建标签，然后设置：标题／图标／标签页布局
        th.addTab(th.newTabSpec("tab2").setIndicator("注册", null).setContent(R.id.linearLayout));
    }

    private void initEt() {
        UserNameEt = (EditText) findViewById(R.id.UserID);
        PassWordEt = (EditText) findViewById(R.id.PassWord);
        NickNameEt = (EditText) findViewById(R.id.UserName);

        LoginID = (EditText) findViewById(R.id.et_UserID);
        LoginPassword = (EditText) findViewById(R.id.et_PassWord);
    }

    private void initBtn() {
        RegBtn = (Button) findViewById(R.id.btn_Reg);
        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册按钮点击事件
                UserName = UserNameEt.getText().toString().toLowerCase();
                PassWord = PassWordEt.getText().toString();
                NickName = NickNameEt.getText().toString();
                if (!UserName.equals("") && !PassWord.equals("") && !NickName.equals("")) {
                    Map<String, String> values = new HashMap<>();
                    values.put("UserName", UserName);
                    values.put("PassWord", PassWord);
                    values.put("NickName", NickName);
                    values.put("UserType", "用户");
                    http.Request(METHOD_INSERT, values);
                } else {
                    Toast.makeText(Register_Login.this, "请完整填写", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LoginBtn = (Button) findViewById(R.id.btn_Login);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserName = LoginID.getText().toString();
                PassWord = LoginPassword.getText().toString();
                CheckLoginState(LoginID.getText().toString(), LoginPassword.getText().toString());
            }
        });
    }

    //记录登陆状态
    private void RememberState(String name, String pass) {
        SharedPreferences mySP = getSharedPreferences("State", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySP.edit();
        if (mySP.getString("UserName", "").equals("")) {
            editor.putString("UserName", name);
            editor.putString("PassWord", pass);
            editor.commit();
        }
    }

    //检查登陆信息
    private void CheckLoginState() {
        SharedPreferences SP = getSharedPreferences("State", Activity.MODE_PRIVATE);
        String name = SP.getString("UserName", "");
        String word = SP.getString("PassWord", "");
        LoginID.setText(name);
        LoginPassword.setText(word);
        if (!name.equals("") && !word.equals("")) {
            Map<String, String> values = new HashMap<>();
            values.put("UserName", name);
            values.put("PassWord", word);
            http_login.Request(METHOD_SELECT, values);
        }
    }

    private void CheckLoginState(String ID, String Pass) {
        if (!ID.equals("") && !Pass.equals("")) {
            Map<String, String> values = new HashMap<>();
            values.put("UserName", ID);
            values.put("PassWord", Pass);
            http_login.Request(METHOD_SELECT, values);
        } else {
            Toast.makeText(Register_Login.this, "请完整填写", Toast.LENGTH_SHORT).show();
        }
    }

    private void StartMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
