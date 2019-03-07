package Widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by FengRui on 2016/2/17.
 */
public class Back extends Button {
    OnClickListener onClickListener;

    public Back(Context context) {
        super(context);
    }

    public Back(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Back(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setContext(final Context context) {
        if (context != null)
            setOnClickListener(onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context != null)
                        ((Activity) context).finish();
                }
            });
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }
}
