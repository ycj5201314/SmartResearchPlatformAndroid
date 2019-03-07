package Info;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by FengRui on 2016/2/15.
 */
public class RaisedInfo {
    private String Target;
    private String Brief;
    private String DateTime;
    private HashMap<String, String> SupportAndRepay;
    private String HadMoney;
    private Bitmap ImgBrief;

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getBrief() {
        return Brief;
    }

    public void setBrief(String brief) {
        Brief = brief;
    }

    public String getHadMoney() {
        return HadMoney;
    }

    public void setHadMoney(String hadMoney) {
        HadMoney = hadMoney;
    }

    public Bitmap getImgBrief() {
        return ImgBrief;
    }

    public void setImgBrief(Bitmap imgBrief) {
        ImgBrief = imgBrief;
    }

    public HashMap<String, String> getSupportAndRepay() {
        return SupportAndRepay;
    }

    public void setSupportAndRepay(HashMap<String, String> supportAndRepay) {
        SupportAndRepay = supportAndRepay;
    }

    public String getTarget() {
        return Target;
    }

    public void setTarget(String target) {
        Target = target;
    }

}
