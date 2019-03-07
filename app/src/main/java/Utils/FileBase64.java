package Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/28.
 */
public class FileBase64 {

    /*文件转Base64*/
    public static String encodeBase64File(String path) {
        byte[] buffer = null;
        try {
            File file = new File(path);
            FileInputStream inputFile = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(buffer, Base64.NO_WRAP);
    }

    /*Base64转文件*/
    public static void decoderBase64File(String base64Code, String savePath) {
        String AbsolutePath = Environment.getExternalStorageDirectory().getPath();
        byte[] buffer = Base64.decode(base64Code, Base64.NO_WRAP);
        try {
            FileOutputStream out = new FileOutputStream(AbsolutePath + savePath);
            out.write(buffer);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*根据uri获取路径*/
    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = 0;
                if (cursor != null) {
                    column_index = cursor.getColumnIndexOrThrow("_data");
                }
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                Log.v("路径转换出错", e.getMessage());
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
}
