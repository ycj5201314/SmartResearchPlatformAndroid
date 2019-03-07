package Widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.partner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Info.ChatMsgInfo;
import Interface.RequestFunc;
import Soap.WebService;
import Utils.ImageBase64;
import Utils.WriteRead_Image;

/**
 * Created by FengRui on 2016/2/22.
 */
public class ChatListView extends ListView {
    Activity activity;
    public List<ChatMsgInfo> allList = new ArrayList<>();
    ChatListView listView;
    String YouID;
    Bitmap myFace, youFace;
    String thisID;
    public MyAdapter adapter;
    final String METHOD_GET_FACE = "DownFaceImage";
    WebService http_face = new WebService(new RequestFunc() {
        @Override
        public void Func() {
            if (http_face.Result != null && !http_face.Result.equals("NoExists")) {
                //缓存图片
                Bitmap bitmap = ImageBase64.base64ToBitmap(http_face.Result);
                WriteRead_Image wri = new WriteRead_Image("FaceImg/", thisID);
                wri.setBitMap(bitmap);
                wri.saveBitmap();

                if (thisID.equals(YouID))
                    youFace = bitmap;
                else
                    myFace = bitmap;
            }
        }
    });

    public ChatListView(Context context) {
        super(context);
    }

    public ChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(Context context, List<ChatMsgInfo> list, String YouID, String MyID) {
        activity = (Activity) context;
        allList = list;
        this.YouID = YouID;
        getFace(YouID);
        getFace(MyID);

        adapter = new MyAdapter(context);
        listView = (ChatListView) activity.findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    //读取头像
    private void getFace(String ID) {
        thisID = ID;
        WriteRead_Image wri = new WriteRead_Image("FaceImg/", ID);
        if (!wri.Exists()) {
            HashMap<String, String> values = new HashMap<>();
            values.put("UserID", YouID);
            http_face.Request(METHOD_GET_FACE, values);
        } else {
            if (ID.equals(YouID))
                youFace = wri.readBitmap();
            else
                myFace = wri.readBitmap();
        }
    }

    //Item数据绑定,按钮监听
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context c) {
            this.inflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return allList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int pos, View myView, ViewGroup parent) {
            ViewHolder holder;
            boolean you = allList.get(pos).getUserID().equals(YouID);
            holder = new ViewHolder();
            if (you) {
                myView = inflater.inflate(R.layout.friends_contact_chat_you, null);
            } else {
                myView = inflater.inflate(R.layout.friends_contact_chat_me, null);
            }


            holder.face = (CircleImageView) myView.findViewById(R.id.face);
            holder.message = (TextView) myView.findViewById(R.id.tv_message);
            holder.time = (TextView) myView.findViewById(R.id.tv_time);
            holder.message.setText(allList.get(pos).getMessage());
            holder.time.setText(allList.get(pos).getDateTime());
            if (you)
                holder.face.setImageBitmap(youFace);
            else
                holder.face.setImageBitmap(myFace);
            return myView;
        }

        //ViewHolder重用视图,减少findViewById次数
        private class ViewHolder {
            CircleImageView face;
            TextView message;
            TextView time;
        }
    }
}
