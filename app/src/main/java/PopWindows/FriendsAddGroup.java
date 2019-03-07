package PopWindows;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.partner.R;

import java.util.HashMap;

import Interface.RequestFunc;
import Soap.WebService;

/**
 * Created by FengRui on 2016/2/19.
 */
public class FriendsAddGroup extends PopupWindow {
    private View mView = null;
    private WindowManager mWindowManager = null;
    private Context context;
    Activity activity;
    public View view;
    public Boolean isShown = false;
    private String UID, YouID, Group = "我的好友";
    String[] groupList;
    private final String METHOD_GET_GROUP = "GetGroup";
    private final String METHOD_ADD_FRIEND = "AddFriend";
    WebService http_group, http_add;

    //显示弹出框
    public FriendsAddGroup(Context ct, String UID, String YouID) {
        this.context = ct;
        this.UID = UID;
        this.YouID = YouID;
        activity = (Activity) context;
        if (isShown) {
            return;
        }
        createSOAP();
        isShown = true;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView();

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        params.flags = flags;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        params.windowAnimations = android.R.style.Animation_Translucent;
        mWindowManager.addView(mView, params);

        HashMap<String, String> values = new HashMap<>();
        values.put("UserID", UID);
        http_group.Request(METHOD_GET_GROUP, values);
    }

    //隐藏弹出框
    public void hidePopupWindow() {
        if (isShown && null != mView) {
            mWindowManager.removeView(mView);
            isShown = false;
        }
    }

    //初始化视图
    private View setUpView() {
        view = LayoutInflater.from(context).inflate(R.layout.friends_add_group, null);
        Button btn_closePop = (Button) view.findViewById(R.id.close);
        Button btn_add = (Button) view.findViewById(R.id.addFriend);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> values = new HashMap<>();
                values.put("MyID", UID);
                values.put("YouID", YouID);
                values.put("Group", Group);
                http_add.Request(METHOD_ADD_FRIEND, values);
            }
        });

        btn_closePop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
            }
        });

        // 点击窗口外部区域可消除
        final View popupWindowView = view.findViewById(R.id.popup_window);// 非透明区域
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                Rect rect = new Rect();
                popupWindowView.getGlobalVisibleRect(rect);
                if (!rect.contains(x, y)) {
                    hidePopupWindow();
                }
                return false;
            }
        });
        return view;
    }

    //显示分组
    private void showGroup(){
        RadioGroup group = (RadioGroup) view.findViewById(R.id.radio);

        for (int i = 0; i < groupList.length; i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setPadding(80, 0, 0, 0);                 // 设置文字距离按钮四周的距离
            radioButton.setText(groupList[i]);
            group.addView(radioButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) view.findViewById(checkedId);
                Group = rb.getText().toString();
            }
        });
    }


    //创建SOAP
    private void createSOAP() {
        http_group = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_group.Result != null) {
                    groupList = http_group.Result.split("｜");
                    showGroup();
                }
            }
        });

        http_add = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http_add.Result != null && http_add.Result.equals("true")) {
                    Toast.makeText(context, "添加成功!", Toast.LENGTH_LONG).show();
                    hidePopupWindow();
                } else {
                    Toast.makeText(context, "已经是你的好友了..", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
