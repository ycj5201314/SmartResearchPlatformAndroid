package PopWindows;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.administrator.partner.R;

import java.util.HashMap;

import Interface.RequestFunc;
import Soap.WebService;

/**
 * Created by FengRui on 2016/2/7.
 */
public class DataDownloadDetailComment extends PopupWindow {
    public View view;
    private Context context;
    private String DataID, UID;
    private final String METHOD_UPDATE_COMMENT = "UpdateComment";
    WebService http;
    CallBack callBack;

    //显示弹出框
    public DataDownloadDetailComment(Context ct, String dataID, String uid, CallBack cb) {
        callBack = cb;
        this.context = ct;
        DataID = dataID;
        UID = uid;
        createSOAP();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.data_download_detail_comment, null);
        Button btn_closePop = (Button) view.findViewById(R.id.close);
        Button btn_sendComment = (Button) view.findViewById(R.id.sendComment);
        final EditText et_Comment = (EditText) view.findViewById(R.id.et_comment);

        //提交评价
        btn_sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> values = new HashMap<>();
                values.put("UserID", UID);
                values.put("DataID", DataID);
                values.put("Comment", et_Comment.getText().toString());
                http.Request(METHOD_UPDATE_COMMENT, values);
            }
        });
        //销毁弹出框
        btn_closePop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(android.R.style.Animation_InputMethod);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable();
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    //创建SOAP
    private void createSOAP() {
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null && http.Result.equals("true")) {
                    Toast.makeText(context, "评价成功", Toast.LENGTH_SHORT).show();
                    callBack.CallBackRespond();
                    dismiss();
                } else {
                    Toast.makeText(context, "请重试..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //回调抽象类,在调用类中重写,在POP中调用
    public interface CallBack {
        void CallBackRespond();
    }

}
