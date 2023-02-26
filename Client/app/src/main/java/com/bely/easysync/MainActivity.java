package com.bely.easysync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bely.easysync.Utils.MsgItem;

public class MainActivity extends AppCompatActivity implements Network.NetworkStatusListener {
    EditText mText;
    TextView mConnectingTxt;
    TextView mIPAddrTxt;
    Handler mHandler;
    ImageView mCfgIcon;
    List<String> mMsgList = new ArrayList<>();
    MyAdapter mMyAdapter;
    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = findViewById(R.id.editTxt);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mConnectingTxt = findViewById(R.id.connecting);
        mIPAddrTxt = findViewById(R.id.ipaddr);
        mCfgIcon = findViewById(R.id.imgCfg);
        mHandler = new Handler();
        mHandler.postDelayed(mUpdateStatusRunnable, 100);

        mRecyclerView = findViewById(R.id.historylist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);
        Network.getInstance().setNetworkStatusListener(this);
    }



    private Runnable mUpdateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            updateConnecting();
        }
    };

    int connectingCounter = 0;
    private void updateConnecting() {
        mIPAddrTxt.setText(Utils.getSavedIP());
        if (!Network.getInstance().isConnected()) {
            mConnectingTxt.setVisibility(View.VISIBLE);
            mCfgIcon.setImageResource(R.drawable.edit);
            if (connectingCounter > 3) {
                connectingCounter = 0;
            }
            String p = "";
            if (connectingCounter == 0) {
                p = "<";
            }
            else if (connectingCounter == 1) {
                p = "<=";
            } else if (connectingCounter == 2) {
                p = "<==";
            } else {
                p = "<===";
            }
            connectingCounter++;
            mConnectingTxt.setText(p);

            mHandler.postDelayed(mUpdateStatusRunnable, 700);
        } else {
            mConnectingTxt.setVisibility(View.GONE);
            mHandler.removeCallbacksAndMessages(null);
            connectingCounter = 0;
            mConnectingTxt.setVisibility(View.GONE);
            mCfgIcon.setImageResource(R.drawable.connected);
        }
    }

    public void sendMsg(View view) {
        if (mText.getText() != null && !TextUtils.isEmpty(mText.getText().toString())) {
            Network.getInstance().sendMsg(mText.getText().toString());
            mMyAdapter.addData(mText.getText().toString());
            mText.getText().clear();

            int position = mMyAdapter.getItemCount() - 1;
            //mRecyclerView.scrollToPosition(position);
            mRecyclerView.smoothScrollToPosition(position);

        } else {
            Toast.makeText(this, "Cannot send empty info", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyItem(View v) {
        int position = (int)v.getTag();
        if (position >= 0 && position < Utils.mDataList.size()) {
            String content = Utils.mDataList.get(position).content;
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", content);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }


    public void setIP(View view) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        EditText et = (EditText) getLayoutInflater().inflate(R.layout.ipinput, null);
        ab.setView(et);
        ab.setTitle("Input IP");
        ab.setCancelable(false);
        String myip = getLocalIpAddress();
        String serverIp = myip.substring(0, myip.lastIndexOf('.')+1);
        et.setText(serverIp);
        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (et.getText() != null) {
                    String ip = et.getText().toString();
                    if (TextUtils.isEmpty(ip) || !isValidIpAddress(ip)) {
                        Toast.makeText(MainActivity.this,  "Invalid input", Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.saveIP(ip);
                        Network.getInstance().setIP(ip);
                    }
                }
            }
        });
        ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ab.show();
        et.requestFocus();
        et.setSelection(et.getText().length());
    }

    private boolean isValidIpAddress(String input) {
        String pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        return input.matches(pattern);
    }

    @Override
    public void onNetworkStatusChanged() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mUpdateStatusRunnable, 300);
    }

    public static String getLocalIpAddress() {
        try {

            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {

                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {

                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        public MyAdapter() {
        }

        private String getTime() {
            long millis = System.currentTimeMillis();  // 当前时间的毫秒数

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String time = sdf.format(new Date(millis));

            return time;
        }
        public void addData(String msg) {
            MsgItem item = new MsgItem();
            item.content = msg;
            item.time = getTime();
            Utils.mDataList.add(item);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 创建一个视图并将其存储在ViewHolder中
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msgitem, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            // 将数据设置到ViewHolder中
            holder.mTime.setText(Utils.mDataList.get(position).time);
            holder.mContent.setText(Utils.mDataList.get(position).content);
            holder.itemView.setTag(position);
            holder.itemView.setOnLongClickListener((v) -> {
                copyItem(v);
                return false;
            });
        }

        @Override
        public int getItemCount() {
            return Utils.mDataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mTime;
            public TextView mContent;

            public MyViewHolder(View itemView) {
                super(itemView);
                mTime = itemView.findViewById(R.id.txtTime);
                mContent = itemView.findViewById(R.id.txtContent);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
        Network.getInstance().setNetworkStatusListener(null);
        Network.getInstance().onGoingToBackground();
    }

    @Override
    public void onStart() {
        super.onStart();
        Network.getInstance().setNetworkStatusListener(this);
        Network.getInstance().onGoingToForeground();
    }

}