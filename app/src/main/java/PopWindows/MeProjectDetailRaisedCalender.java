package PopWindows;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.administrator.partner.R;

import java.util.Calendar;

/**
 * Created by FengRui on 2016/2/12.
 */
public class MeProjectDetailRaisedCalender extends PopupWindow {
    private Context context;
    public View view;
    CallBack callBack;
    int Y, M, D;//系统日期
    int year, month, day;//待返回日期

    //显示弹出框
    public MeProjectDetailRaisedCalender(Context ct, CallBack cb) {
        callBack = cb;
        this.context = ct;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.me_project_detail_raised_calendar, null);
        Button btn_closePop = (Button) view.findViewById(R.id.close);
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.calendar);

        //获取当前日期
        Calendar c = Calendar.getInstance();
        Y = c.get(Calendar.YEAR);
        M = c.get(Calendar.MONTH);
        D = c.get(Calendar.DAY_OF_MONTH);

        //获取选择的日期
        datePicker.init(Y, M, D, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int y, int m, int d) {
                year = y;
                month = m;
                day = d;
            }
        });
        //销毁弹出框并返回日期
        btn_closePop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (year > Y || (year == Y && month > M) || (year == Y && month == M && day > D)) {
                    callBack.CallBackRespond(year, month, day);
                    dismiss();
                } else
                    Toast.makeText(context, "请选择至少晚于今天的日期", Toast.LENGTH_LONG).show();
            }
        });

        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(android.R.style.Animation_InputMethod);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    //回调抽象类,在调用类中重写,在POP中调用
    public interface CallBack {
        void CallBackRespond(int year, int month, int day);
    }
}
