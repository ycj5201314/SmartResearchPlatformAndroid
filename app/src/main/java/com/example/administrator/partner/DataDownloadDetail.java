package com.example.administrator.partner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import Info.DataInfo;
import Interface.RequestFunc;
import PopWindows.DataDownloadDetailComment;
import PopWindows.DataDownloadDetailDownload;
import Soap.WebService;
import Widget.Back;

/**
 * Created by Administrator on 2016/1/27.
 */
public class DataDownloadDetail extends Activity {

    final String METHOD_DATA_DETAIL = "ReturnDataDetail";
    final String METHOD_GOOD_BAD = "UpdateGoodOrBad";
    String UID;
    String DataID;
    WebService http_detail, http_goodbad;
    TextView tv_DataName, tv_DataBrief, tv_CommentContent;
    Button btn_Good, btn_Bad, btn_Comment, btn_Download;
    DataInfo dataInfo = new DataInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_download_detail);
        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        UID = sp.getString("UserName", "");
        CreateSOAP();
        initControl();
        RequestHttp();
        bindControl();
        ((Back)findViewById(R.id.btn_back)).setContext(DataDownloadDetail.this);//返回按钮
    }

    //创建SOAP对象
    private void CreateSOAP() {
        http_detail = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_detail.Result != null) {
                    parseResult(http_detail.Result);
                    showDataDetail();
                }
            }
        });
        http_goodbad = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_goodbad.Result != null && http_goodbad.Result.equals("true")) {
                    disabledRankBtn();
                }
            }
        });
    }

    //获取控件
    private void initControl() {
        tv_DataName = (TextView) findViewById(R.id.tv_dataName);
        tv_DataBrief = (TextView) findViewById(R.id.tv_dataBrief);
        tv_CommentContent = (TextView) findViewById(R.id.tv_commentContent);
        btn_Good = (Button) findViewById((R.id.btn_good));
        btn_Bad = (Button) findViewById((R.id.btn_bad));
        btn_Comment = (Button) findViewById((R.id.btn_comment));
        btn_Download = (Button) findViewById((R.id.btn_download));
    }

    //请求数据
    private void RequestHttp() {
        DataID = getIntent().getStringExtra("DataID");//获取上一个窗体传来的资料编号
        HashMap<String, String> values = new HashMap<>();
        values.put("DataID", DataID);
        values.put("UserID", UID);
        http_detail.Request(METHOD_DATA_DETAIL, values);
    }

    //解析数据
    private void parseResult(String result) {
        String[] arr = result.split("｜");
        for (int i = 0; i < arr.length; i += 9) {
            dataInfo.setDataID(arr[i]);
            dataInfo.setDataName(arr[i + 1]);
            dataInfo.setDataBrief(arr[i + 2]);
            dataInfo.setCommentContent(arr[i + 3]);
            dataInfo.setGoodRate(arr[i + 4]);
            dataInfo.setBadRate(arr[i + 5]);
            dataInfo.setDataURL(arr[i + 6]);
            dataInfo.setDataDoc(arr[i + 7]);
            dataInfo.setRankCheck(arr[i + 8]);
        }
    }

    //显示数据
    private void showDataDetail() {
        tv_DataName.setText(dataInfo.getDataName());
        tv_DataBrief.setText(dataInfo.getDataBrief());
        tv_CommentContent.setText(dataInfo.getCommentContent());
        btn_Good.setText("有用　" + dataInfo.getGoodRate());
        btn_Bad.setText("无用　" + dataInfo.getBadRate());
        if (Integer.parseInt(dataInfo.getRankCheck()) > 0)
            disabledRankBtn();
    }

    //按钮事件
    private void bindControl() {
        btn_Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DataDownloadDetailDownload(DataDownloadDetail.this, dataInfo.getDataURL(), dataInfo.getDataDoc());
            }
        });
        btn_Good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRanking("0");
            }
        });
        btn_Bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRanking("1");
            }
        });
        btn_Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示窗口
                DataDownloadDetailComment.CallBack callBack = new DataDownloadDetailComment.CallBack() {
                    @Override
                    public void CallBackRespond() {
                        RequestHttp();
                    }
                };
                DataDownloadDetailComment pop = new DataDownloadDetailComment(DataDownloadDetail.this, DataID, UID, callBack);//初始化对象
                pop.showAtLocation(pop.view.findViewById(R.id.pop), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });
    }

    //提交有用无用指数
    private void updateRanking(String type) {
        HashMap<String, String> values = new HashMap<>();
        values.put("DataID", dataInfo.getDataID());
        values.put("UserID", UID);
        values.put("Type", type);
        http_goodbad.Request(METHOD_GOOD_BAD, values);
        btnUpdate(type);
    }

    //更新指数按钮
    private void btnUpdate(String type) {
        if (type.equals("0"))
            btn_Good.setText("有用　" + (Integer.parseInt(dataInfo.getGoodRate()) + 1));
        else
            btn_Bad.setText("无用　" + (Integer.parseInt(dataInfo.getBadRate()) + 1));
    }

    //禁用评级按钮
    private void disabledRankBtn() {
        btn_Good.setEnabled(false);
        btn_Bad.setEnabled(false);
    }
}
