package Widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.Stack;

/**
 * Created by FengRui on 2016/2/14.
 */
public class DynamicScrollView extends ScrollView {
    final int maxShowPixel = 2000;//ImageView分段分辨率
    Bitmap bm;
    LinearLayout ly;
    Stack<Bitmap> stack = new Stack<>();//用来暂存Bitmap资源
    Context context;

    public DynamicScrollView(Context context) {
        super(context);
    }

    public DynamicScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //传入资源
    public void initResource(Bitmap bm, LinearLayout ly, Context context) {
        this.bm = bm;
        this.ly = ly;
        this.context = context;
        cutImage();
    }

    //分辨率过大的Bitmap剪裁成多个小分辨率的Bitmap,再显示到ImageView
    public void cutImage() {
        if (bm != null)
            for (int y = 0; y <= bm.getHeight(); y += maxShowPixel) {
                int height;//剪裁的高度
                if (y + maxShowPixel <= bm.getHeight()) {
                    height = maxShowPixel;
                } else {
                    height = bm.getHeight() - y;
                }
                Bitmap newBm = Bitmap.createBitmap(bm, 0, y, bm.getWidth(), height, null, false);
                ImageView iv = new ImageView(context);
                iv.setAdjustViewBounds(true);
                iv.setImageBitmap(newBm);
                ly.addView(iv);
            }
    }

    //滚动监听
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        if (ly == null)
            return;
        for (int i = 0; i < ly.getChildCount(); i++) {
            int[] loc = new int[2];
            ImageView iv = (ImageView) ly.getChildAt(i);//取得控件
            iv.getLocationInWindow(loc);//取得在父控件中的坐标
            if (loc[1] < -maxShowPixel && iv.getDrawable() != null) {
                iv.setLayoutParams(new LinearLayout.LayoutParams(iv.getWidth(), iv.getHeight()));//保持空间占位
                stack.push(((BitmapDrawable) iv.getDrawable()).getBitmap());//Bitmap资源暂存到栈中
                iv.setImageResource(0);//释放渲染资源
            } else if (loc[1] >= -maxShowPixel && iv.getDrawable() == null) {
                iv.setImageBitmap(stack.pop());
            }
        }
    }
}
