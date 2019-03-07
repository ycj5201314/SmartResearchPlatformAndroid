package Info;

/**
 * Created by FengRui on 2016/2/3.
 */
public class DataInfo {
    private String DataName;
    private String DataBrief;
    private String CommentContent;
    private String GoodRate;
    private String BadRate;
    private String DataURL;
    private String DataDoc;
    private String DataID;

    public String getRankCheck() {
        return RankCheck;
    }

    public void setRankCheck(String rankCheck) {
        RankCheck = rankCheck;
    }

    private String RankCheck;

    public String getDataID() {
        return DataID;
    }

    public void setDataID(String dataID) {
        DataID = dataID;
    }

    public String getCommentContent() {
        return CommentContent;
    }

    public void setCommentContent(String commentContent) {
        CommentContent = commentContent;
    }

    public String getDataBrief() {
        return DataBrief;
    }

    public void setDataBrief(String dataBrief) {
        DataBrief = dataBrief;
    }

    public String getDataDoc() {
        return DataDoc;
    }

    public void setDataDoc(String dataDoc) {
        DataDoc = dataDoc;
    }

    public String getDataName() {
        return DataName;
    }

    public void setDataName(String dataName) {
        DataName = dataName;
    }

    public String getDataURL() {
        return DataURL;
    }

    public void setDataURL(String dataURL) {
        DataURL = dataURL;
    }

    public String getGoodRate() {
        return GoodRate;
    }

    public void setGoodRate(String goodRate) {
        GoodRate = goodRate;
    }

    public String getBadRate() {
        return BadRate;
    }

    public void setBadRate(String badRate) {
        BadRate = badRate;
    }
}
