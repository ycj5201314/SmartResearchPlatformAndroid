package PopWindows;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.partner.R;

import java.util.HashMap;

import Interface.RequestFunc;
import Soap.WebService;
import Utils.FileBase64;
import Utils.OpenURL;

/**
 * Created by FengRui on 2016/2/3.
 */
public class DataDownloadDetailDownload {
    private View mView = null;
    private WindowManager mWindowManager = null;
    private Context context;
    Activity activity;
    public Boolean isShown = false;
    private String URL, DocName;
    private final String METHOD_RETURN_FILE = "ReturnFile";
    WebService http;

    //显示弹出框
    public DataDownloadDetailDownload(Context ct, String url, String docName) {
        this.context = ct;
        activity = (Activity) context;
        if (isShown) {
            return;
        }
        createSOAP();
        isShown = true;
        URL = url;
        DocName = docName;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView();

        final LayoutParams params = new WindowManager.LayoutParams();
        params.type = LayoutParams.TYPE_SYSTEM_ALERT;
        int flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        params.flags = flags;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        params.windowAnimations = android.R.style.Animation_InputMethod;
        mWindowManager.addView(mView, params);
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
        View view = LayoutInflater.from(context).inflate(R.layout.data_download_detail_download, null);
        Button btn_closePop = (Button) view.findViewById(R.id.close);
        Button btn_url = (Button) view.findViewById(R.id.tv_url);
        Button btn_doc = (Button) view.findViewById(R.id.tv_dataDoc);

        if (URL.equals("暂无"))
            btn_url.setText("无下载地址");
        else {
            btn_url.setText(URL);
            btn_url.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OpenURL(context, URL);
                    hidePopupWindow();
                }
            });
        }
        if (DocName.equals("暂无"))
            btn_doc.setText("无附件可下载");
        else {
            btn_doc.setText(DocName.substring(DocName.indexOf("－") + 1));
            btn_doc.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> values = new HashMap<>();
                    values.put("FileName", DocName);
                    http.Request(METHOD_RETURN_FILE, values);
                }
            });
        }

        btn_closePop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
            }
        });

        // 点击窗口外部区域可消除
        final View popupWindowView = view.findViewById(R.id.popup_window);// 非透明区域
        view.setOnTouchListener(new OnTouchListener() {
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

    //创建SOAP
    private void createSOAP() {
        http = new WebService(new RequestFunc() {
            @Override
            public void Func() {
                if (http.Result != null) {
                    String docPath = context.getString(R.string.docPath);
                    FileBase64.decoderBase64File(http.Result, docPath + DocName.substring(DocName.indexOf("｜") + 1));
                    Toast.makeText(context, "已保存到" + docPath, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
