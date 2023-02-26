package com.bely.easysync;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<MsgItem> mDataList = new ArrayList<>();

    public static String getSavedIP() {
        SharedPreferences sp = SyncApplication.getContext().getSharedPreferences("KEY_CONFIG", Context.MODE_PRIVATE);
        return sp.getString("KEY_IP", "0.0.0.0");
    }

    static void saveIP(String ip) {
        SharedPreferences sp = SyncApplication.getContext().getSharedPreferences("KEY_CONFIG", Context.MODE_PRIVATE);
        sp.edit().putString("KEY_IP", ip).commit();
    }

    public static class MsgItem {
        public String time;
        public String content;
    }
}
