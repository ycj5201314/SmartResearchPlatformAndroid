package Info;

/**
 * Created by FengRui on 2016/2/22.
 */
public class ChatMsgInfo {
    private String UserID;
    private String Message;
    private String DateTime;

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

}
