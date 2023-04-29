package com.bely.easysync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
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

public class MainActivity extends AppCompatActivity implements Network.UIEventListener {
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

        if (savedInstanceState == null) {
            // 应用程序尚未启动，处理接收到的共享内容
            handleIncomingIntent(getIntent());
        }
    }

    private void setFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void exitFulLScreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
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
            mMyAdapter.addData(mText.getText().toString(), true);
            mText.getText().clear();

            int position = mMyAdapter.getItemCount() - 1;
            //mRecyclerView.scrollToPosition(position);
            mRecyclerView.smoothScrollToPosition(position);

        } else {
            Toast.makeText(this, "Cannot send empty info", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 应用程序已经启动，处理接收到的共享内容
        handleIncomingIntent(intent);
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        try {
            // 通过ContentResolver打开Uri
            InputStream inputStream = getContentResolver().openInputStream(uri);
            // 从输入流解码Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void handleIncomingIntent(Intent intent) {
        Thread t = new Thread(() -> {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    // 处理接收到的图片
                    Bitmap bmp = getBitmapFromUri(imageUri);
                    mHandler.post(() -> {
                        mMyAdapter.addData(bmp, true);
                        int position = mMyAdapter.getItemCount() - 1;
                        //mRecyclerView.scrollToPosition(position);
                        mRecyclerView.smoothScrollToPosition(position);
                    });
                    Network.getInstance().sendImg(bmp);
                }
            }
        });
        t.start();
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

    public void saveImg(View v) {
        int position = (int)v.getTag();
        if (position >= 0 && position < Utils.mDataList.size()) {
            Utils.saveImg(Utils.mDataList.get(position).img);
        }
    }

    public void close(View v) {
        exitFulLScreen();
        mImgViewWindow.dismiss();
    }

    public void showImg(View v) {
        int position = (int) v.getTag();
        if (position >= 0 && position < Utils.mDataList.size()) {

            Utils.setBitmapClicked(Utils.mDataList.get(position).img);


            //Intent intent = new Intent();
            //intent.setClass(this, ImgViewActivity.class);
            //startActivity(intent);

            ImgViewWindow popupWindow = new ImgViewWindow(MainActivity.this);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            mImgViewWindow = popupWindow;
            setFullScreen();
        }
    }
    PopupWindow mImgViewWindow = null;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mImgViewWindow != null) {
                mImgViewWindow.dismiss();
                mImgViewWindow = null;
                exitFulLScreen();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
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

    @Override
    public void onGotMsg(Bitmap img) {
        mHandler.post(()->mMyAdapter.addData(img, false));
    }

    @Override
    public void onGotMsg(String msg) {
        mHandler.post(()->mMyAdapter.addData(msg, false));
    }

    @Override
    public void onSendError() {
        mHandler.post(()->{
            Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show();
        });
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
        public void addData(String msg, boolean sent) {
            MsgItem item = new MsgItem();
            item.type = 1;
            item.content = msg;
            item.time = getTime();
            item.sent = sent;
            Utils.mDataList.add(item);
            notifyDataSetChanged();
            int position = mMyAdapter.getItemCount() - 1;
            //mRecyclerView.scrollToPosition(position);
            mRecyclerView.smoothScrollToPosition(position);
        }

        public void addData(Bitmap img, boolean sent) {
            MsgItem item = new MsgItem();
            item.type = 0;
            item.img = img;
            item.sent = sent;
            item.time = getTime();
            Utils.mDataList.add(item);
            notifyDataSetChanged();
            int position = mMyAdapter.getItemCount() - 1;
            //mRecyclerView.scrollToPosition(position);
            mRecyclerView.smoothScrollToPosition(position);
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
            if (Utils.mDataList.get(position).type == 0) {
                //image
                holder.itemView.setTag(position);
                holder.itemView.setOnClickListener((v) -> {
                    showImg(v);
                    return;
                });
                holder.mContent.setVisibility(View.GONE);

                holder.itemView.setOnLongClickListener((v) -> {
                    saveImg(v);
                    return true;
                });
                holder.mImage.setImageBitmap(Utils.mDataList.get(position).img);

            } else {
                //text
                holder.mImage.setVisibility(View.GONE);
                holder.mContent.setText(Utils.mDataList.get(position).content);
                holder.itemView.setTag(position);
                holder.itemView.setOnLongClickListener((v) -> {
                    copyItem(v);
                    return false;
                });
            }


            if (Utils.mDataList.get(position).sent) {
                adjustAlignmentToRight(holder);
            }

        }

        private void adjustAlignmentToRight(MyViewHolder holder) {
            ConstraintSet set = new ConstraintSet();
            set.clone((ConstraintLayout) holder.itemView);
            set.clear(holder.mContent.getId(), ConstraintSet.START);
            set.clear(holder.mTime.getId(), ConstraintSet.START);
            set.clear(holder.mImage.getId(), ConstraintSet.START);
            set.connect(holder.mContent.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(holder.mTime.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(holder.mImage.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.applyTo((ConstraintLayout) holder.itemView);
        }

        @Override
        public int getItemCount() {
            return Utils.mDataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mTime;
            public TextView mContent;
            public ImageView mImage;

            public MyViewHolder(View itemView) {
                super(itemView);
                mTime = itemView.findViewById(R.id.txtTime);
                mContent = itemView.findViewById(R.id.txtContent);
                mImage = itemView.findViewById(R.id.image);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //mHandler.removeCallbacksAndMessages(null);
        //Network.getInstance().setNetworkStatusListener(null);
        //Network.getInstance().onGoingToBackground();
    }

    @Override
    public void onStart() {
        super.onStart();
        Network.getInstance().setNetworkStatusListener(this);
        Network.getInstance().onGoingToForeground();
    }

}