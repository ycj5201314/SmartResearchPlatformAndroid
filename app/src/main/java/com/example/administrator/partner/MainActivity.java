package com.example.administrator.partner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import ActivityLogic.EventHelper;
import Interface.RequestFunc;
import Soap.WebService;
import Utils.ImageBase64;
import Utils.WriteRead_Image;
import Widget.CircleImageView;


public class MainActivity extends AppCompatActivity {
    EventHelper eh;
    WebService http;
    String uid;
    String face;
    final String METHOD_UP_IMAGE = "UpFaceImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initFunc();
    }

    private void initFunc() {
        SharedPreferences sp = getSharedPreferences("State", MODE_PRIVATE);
        uid = sp.getString("UserName", "");
        eh = new EventHelper(this);
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null)
                    if (http.Result.equals("true")) {
                        Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        WriteRead_Image wri = new WriteRead_Image("FaceImg/", uid);
                        wri.setBitMap(ImageBase64.base64ToBitmap(face));
                        wri.saveBitmap();
                    } else {
                        Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        CreateFolder();
    }

    //创建文件夹
    private void CreateFolder() {
        String AbsolutePath = Environment.getExternalStorageDirectory().getPath();//手机外存路径
        File imgfolder = new File(AbsolutePath + getString(R.string.imgPath));
        if (!imgfolder.exists()) {
            imgfolder.mkdirs();
        }
        File docfolder = new File(AbsolutePath + getString(R.string.docPath));
        if (!docfolder.exists()) {
            docfolder.mkdirs();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //设置头像的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            CircleImageView imageView = (CircleImageView) eh.view.findViewById(R.id.FaceImg);
            Bitmap bitmap = data.getParcelableExtra("data");
            imageView.setImageBitmap(bitmap);
            //转成base64
            face = ImageBase64.bitmapToBase64(bitmap, 90);
            HashMap<String, String> values = new HashMap<>();
            values.put("UserID", uid);
            values.put("face", face);
            http.Request(METHOD_UP_IMAGE, values);
        }
    }
}


