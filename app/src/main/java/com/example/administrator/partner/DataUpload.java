package com.example.administrator.partner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import Interface.RequestFunc;
import Soap.WebService;
import Utils.FileBase64;
import Utils.GetFilesSize;
import Utils.OpenURL;
import Widget.Back;

/**
 * Created by Administrator on 2016/1/27.
 */
public class DataUpload extends Activity implements View.OnClickListener {

    final String METHOD_DATA_UPLOAD = "DataUpload";
    String DocName;
    ArrayList<Button> btnList = new ArrayList<>();
    String DocPath;//待上传文件路径
    String DataType;//资料类型
    String UID;
    WebService http = new WebService(new RequestFunc() {
        @Override
        public void Func() {
            if (http.Result != null && http.Result.equals("true")) {
                Toast.makeText(DataUpload.this, "上传完成", Toast.LENGTH_SHORT).show();
                DataUpload.this.finish();
            } else {
                Toast.makeText(DataUpload.this, "上传失败", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_upload);

        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        UID = sp.getString("UserName", "");
        btnBind();
        initSpinner();
        ((Back)findViewById(R.id.btn_back)).setContext(DataUpload.this);//返回按钮
    }

    //初始化按钮和监听
    private void btnBind() {
        int[] idList = {R.id.btn_UploadDoc, R.id.btn_Upload, R.id.yun360, R.id.yunbaidu, R.id.yunweiyun, R.id.yunleshi, R.id.yunyinxing};
        for (int id : idList) {
            btnList.add((Button) findViewById(id));
        }
        for (Button btn : btnList) {
            btn.setOnClickListener(this);
        }
        findViewById(R.id.tv_manual).setOnClickListener(this);
        findViewById(R.id.tv_report).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //申请按钮监听
        switch (v.getId()) {
            case R.id.btn_UploadDoc:
                ChoiceDocFile();
                break;
            case R.id.btn_Upload:
                UploadData();
                break;
            case R.id.yun360:
                new OpenURL(DataUpload.this, getString(R.string.yun360));
                break;
            case R.id.yunbaidu:
                new OpenURL(DataUpload.this, getString(R.string.yunbaidu));
                break;
            case R.id.yunweiyun:
                new OpenURL(DataUpload.this, getString(R.string.yunweiyun));
                break;
            case R.id.yunleshi:
                new OpenURL(DataUpload.this, getString(R.string.yunleshi));
                break;
            case R.id.yunyinxing:
                new OpenURL(DataUpload.this, getString(R.string.yunyinxing));
                break;
            case R.id.tv_manual:
                break;
            case R.id.tv_report:
                break;
        }
    }

    //选择文件并返回文件路径
    private void ChoiceDocFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "选择一个文档以上传"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装一个文件管理器", Toast.LENGTH_SHORT).show();
        }

    }

    //上传资料和附件
    private void UploadData() {
        String dataName = ((EditText) findViewById(R.id.et_dataName)).getText().toString();
        String downloadUrl = ((EditText) findViewById(R.id.et_downloadUrl)).getText().toString();
        String dataBrief = ((EditText) findViewById(R.id.et_dataBrief)).getText().toString();

        if (downloadUrl.length() > 0 && downloadUrl.indexOf("http://") < 0)
            downloadUrl = "http://" + downloadUrl;

        if (dataName.length() > 0 && (downloadUrl.length() > 0 || DocPath != null) && dataBrief.length() > 0) {
            HashMap<String, String> values = new HashMap<>();
            values.put("UserID", UID);//用户账号
            values.put("DataName", dataName);//资料名称
            values.put("DownloadUrl", downloadUrl);//下载地址
            values.put("DataBrief", dataBrief);//资料简介
            //资料类型
            if (DataType != null)
                values.put("DataType", DataType);
            else
                values.put("DataType", DataUpload.this.getResources().getStringArray(R.array.Data_DataType)[0]);
            //文档附件
            if (DocPath != null) {
                Log.v("", DocPath);
                values.put("DocName", DocName);
                values.put("DocBase64", FileBase64.encodeBase64File(DocPath));
            } else {
                values.put("DocName", "null");
                values.put("DocBase64", "null");
            }

            //提交上传
            http.Request(METHOD_DATA_UPLOAD, values);
        } else
            Toast.makeText(DataUpload.this, "须完整填写表格,下载地址和附件至少填充一个", Toast.LENGTH_SHORT).show();
    }

    //下拉控件事件监听
    private void initSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                DataType = DataUpload.this.getResources().getStringArray(R.array.Data_DataType)[pos];//获取选中项,即资料类型
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    //选择文件会话回调,存储文件路径
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data==null)
            return;
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            DocPath = FileBase64.getPath(this, uri);
        }
        //判断文件大小,大于200KB则拒绝上传
        double fileSize = GetFilesSize.getFileOrFilesSize(DocPath, GetFilesSize.SIZETYPE_KB);
        if (fileSize > 200) {
            DocPath = null;
            Toast.makeText(this, "请选择小于200KB的文件,\n此文件大小:" + fileSize + "KB", Toast.LENGTH_SHORT).show();
            btnList.get(0).setText("选择一个小于200KB的文件");
        } else {
            DocName = DocPath.substring(DocPath.lastIndexOf("/") + 1);//文件名
            btnList.get(0).setText(DocName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
