package Widget;

import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.administrator.partner.R;

import java.util.HashMap;

import Info.ProjectInfo;
import Interface.RequestFunc;
import Soap.WebService;

/**
 * Created by Administrator on 2016/1/16.
 */
public class FavoriteCheckBox {
    CheckBox checkBox;
    final String METHOD_PROJECT_FAVORITE = "ChangeProjectFavorite";
    final String METHOD_FAVORITE_STATE = "CheckProjectFavorite";
    WebService http, http0;
    Activity activity;
    String uid;
    ProjectInfo PI;

    public FavoriteCheckBox(Context context, String uid, ProjectInfo PI) {
        activity = (Activity) context;
        this.uid = uid;
        this.PI = PI;
        checkBox = (CheckBox) activity.findViewById(R.id.Favorite);
        CreateHttpObject();
        SetCheckBoxState();
    }

    //创建Soap对象并传入被重写方法的请求对象
    private void CreateHttpObject() {
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result.equals("favorite")) {
                    Toast.makeText(activity, "收藏成功", Toast.LENGTH_SHORT).show();
                } else if (http.Result.equals("unfavorite")) {
                    Toast.makeText(activity, "取消收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "操作失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        http0 = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http0.Result.equals("true")) {
                    checkBox.setChecked(true);
                }
                initCheckBox();
            }
        });
    }

    //检查该用户对该项目的收藏状态
    private void SetCheckBoxState() {
        HashMap<String, String> values = new HashMap<>();
        values.put("UserID", uid);
        values.put("ProjectID", PI.getProjectID());
        http0.Request(METHOD_FAVORITE_STATE, values);
    }

    //初始化控件
    private void initCheckBox() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //提交收藏
                    HashMap<String, String> values = new HashMap<>();
                    values.put("UserID", uid);
                    values.put("ProjectID", PI.getProjectID());
                    values.put("Checked", "true");
                    http.Request(METHOD_PROJECT_FAVORITE, values);
                } else {
                    //取消收藏
                    HashMap<String, String> values = new HashMap<>();
                    values.put("UserID", uid);
                    values.put("ProjectID", PI.getProjectID());
                    values.put("Checked", "false");
                    http.Request(METHOD_PROJECT_FAVORITE, values);
                }
            }
        });
    }
}
