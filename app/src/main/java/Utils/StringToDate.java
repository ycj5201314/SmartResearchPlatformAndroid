package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FengRui on 2016/2/15.
 */
public class StringToDate {
    public static Date toDate(String dateStr) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
