package Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/24.
 */
public class WriteRead_Image {
    File file;
    String SDCardPath;
    String AllPath;
    Bitmap BM;

    public WriteRead_Image(String Folder, String FileName) {
        SDCardPath = Environment.getExternalStorageDirectory().getPath();

        file = new File(SDCardPath + "/.partner/" + Folder, FileName + ".jpg");
        AllPath = SDCardPath + "/.partner/" + Folder + FileName + ".jpg";
    }

    //存图时要调用
    public void setBitMap(Bitmap BM) {
        this.BM = BM;
    }

    //尝试保存图片到本地
    public void saveBitmap() {
        try {
            if(file.exists())
                file.delete();
            FileOutputStream out = new FileOutputStream(file);
            BM.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取一个图片
    public Bitmap readBitmap() {
        try {
            return BitmapFactory.decodeFile(AllPath);
        } catch (Exception e) {
            return null;
        }
    }

    //图片是否存在
    public boolean Exists() {
        if (file.exists())
            return true;
        return false;
    }
}
