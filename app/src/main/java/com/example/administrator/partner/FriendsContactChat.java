package com.example.administrator.partner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Info.ChatMsgInfo;
import Interface.RequestFunc;
import Soap.WebService;
import Utils.ImageBase64;
import Utils.WriteRead_Image;
import Widget.Back;
import Widget.ChatListView;
import Widget.CircleImageView;

/**
 * Created by FengRui on 2016/2/21.
 */
public class FriendsContactChat extends Activity {
    String YouID, MyID, YouName;
    final String METHOD_GET_FACE = "DownFaceImage";
    final String METHOD_GET_MESSAGE = "GetMessage";
    final String METHOD_SEND_MESSAGE = "SendMessage";
    WebService http_face, http_messageList, http_send;
    EditText input;
    Button send;
    CircleImageView face;
    List<ChatMsgInfo> msgInfoList = new ArrayList<>();
    ChatListView chatListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_contact_chat);
        ((Back) findViewById(R.id.btn_back)).setContext(FriendsContactChat.this);//返回按钮
        NoShowKeyBoard();
        initControl();
        createSOAP();
        getData();
        request();
        showInfo();
        btnListener();
        timerRefresh();
    }

    //禁止弹出键盘
    private void NoShowKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    //创建SOAP
    private void createSOAP() {
        http_face = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_face.Result != null && !http_face.Result.equals("NoExists")) {
                    //缓存图片
                    Bitmap bitmap = ImageBase64.base64ToBitmap(http_face.Result);
                    WriteRead_Image wri = new WriteRead_Image("FaceImg/", YouID);
                    wri.setBitMap(bitmap);
                    wri.saveBitmap();
                    //显示图片
                    face.setImageBitmap(bitmap);
                }
            }
        });

        http_messageList = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_messageList.Result != null && !http_messageList.Result.equals("null")) {
                    formatData(http_messageList.Result.split("｜"));
                }
                else{
                    chatListView.setData(FriendsContactChat.this, msgInfoList, YouID, MyID);
                }
            }
        });

        http_send= new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_send.Result != null && !http_send.Result.equals("false")) {

                }
                else {

                }
            }
        });
    }

    //初始化控件
    private void initControl() {
        input = (EditText) findViewById(R.id.input);
        send = (Button) findViewById(R.id.btn_send);
        face = (CircleImageView) findViewById(R.id.face);
        chatListView = (ChatListView) findViewById(R.id.listView);

    }

    //获取数据
    private void getData() {
        Intent intent = getIntent();
        YouName = intent.getStringExtra("YouName");
        YouID = intent.getStringExtra("YouID");
        MyID = intent.getStringExtra("MyID");
    }

    //请求数据
    private void request(){
        HashMap<String, String> values = new HashMap<>();
        values.put("MyID", MyID);
        values.put("YouID", YouID);
        http_messageList.Request(METHOD_GET_MESSAGE, values);
    }

    //规格化数据
    private void formatData(String[] list) {
        ChatMsgInfo msgInfo;
        msgInfoList.clear();
        for (int i = 0; i < list.length; i += 3) {
            msgInfo = new ChatMsgInfo();
            msgInfo.setUserID(list[i]);
            msgInfo.setMessage(list[i + 1]);
            msgInfo.setDateTime(list[i + 2]);
            msgInfoList.add(msgInfo);
        }
        chatListView.setData(FriendsContactChat.this, msgInfoList, YouID, MyID);
    }

    //展示信息
    private void showInfo() {
        ((TextView) findViewById(R.id.tv_userName)).setText(YouName);

        //显示对方头像
        WriteRead_Image wri = new WriteRead_Image("FaceImg/", YouID);
        if (!wri.Exists()) {
            HashMap<String, String> values = new HashMap<>();
            values.put("UserID", YouID);
            http_face.Request(METHOD_GET_FACE, values);
        } else {
            face.setImageBitmap(wri.readBitmap());
        }
    }

    //按钮事件
    private void btnListener() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> values = new HashMap<>();
                values.put("MyID", MyID);
                values.put("YouID", YouID);
                values.put("Message", input.getText().toString());
                http_send.Request(METHOD_SEND_MESSAGE, values);
                //添加到listView
                ChatMsgInfo msgInfo = new ChatMsgInfo();
                msgInfo.setUserID(MyID);
                msgInfo.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                msgInfo.setMessage(input.getText().toString());
                chatListView.allList.add(msgInfo);
                chatListView.adapter.notifyDataSetChanged();
                input.setText("");
            }
        });
    }

    //定时刷新
    private void timerRefresh(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                request();
            }
        }, 10000,10000);
    }
}
