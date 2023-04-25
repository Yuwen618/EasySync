package com.bely.easysync;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static Bitmap mClickedBmp;
    public static List<MsgItem> mDataList = new ArrayList<>();

    public static String getSavedIP() {
        SharedPreferences sp = SyncApplication.getContext().getSharedPreferences("KEY_CONFIG", Context.MODE_PRIVATE);
        return sp.getString("KEY_IP", "0.0.0.0");
    }

    static void saveIP(String ip) {
        SharedPreferences sp = SyncApplication.getContext().getSharedPreferences("KEY_CONFIG", Context.MODE_PRIVATE);
        sp.edit().putString("KEY_IP", ip).commit();
    }

    public static void setBitmapClicked(Bitmap bmp) {
        mClickedBmp = bmp;
    }

    public static Bitmap getBitmapClicked() {
        return mClickedBmp;
    }

    public static class MsgItem {
        public String time;
        public String content;
        public int type;
        public Bitmap img;
        public boolean sent;
    }

    public static void saveImg(Bitmap bmp) {
        File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            MediaScannerConnection.scanFile(SyncApplication.getContext(), new String[]{imageFile.getAbsolutePath()}, null, null);
            Toast.makeText(SyncApplication.getContext(), "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
