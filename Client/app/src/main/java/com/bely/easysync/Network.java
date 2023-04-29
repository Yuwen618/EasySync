package com.bely.easysync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Network {
    private static final String TAG = "Network";
    private static int PORT = 30291;
    private String SERVER = "";
    Handler mHandler;
    private Socket socket;
    private OutputStreamWriter writer;
    private BufferedReader reader;
    private DataInputStream inputstream;
    private static Network instance;
    UIEventListener mlistener;
    public interface UIEventListener {
        void onNetworkStatusChanged();
        void onGotMsg(Bitmap img);
        void onGotMsg(String msg);
        void onSendError();
    }

    public void setNetworkStatusListener(UIEventListener l) {
        mlistener = l;
        if (mlistener != null) {
            mlistener.onNetworkStatusChanged();
        }
    }

    private Network() {
        HandlerThread t = new HandlerThread("network");
        t.start();
        mHandler = new Handler(t.getLooper());
    }

    public void start(String serverIp) {
        SERVER = serverIp;
        if (!("0.0.0.0").equals(SERVER)) {
            mHandler.post(() -> connect());
        }
    }

    public static Network getInstance() {
        if (instance == null) {
            synchronized (Network.class) {
                if (instance == null) {
                    instance = new Network();
                }
            }
        }
        return instance;
    }

    public void setIP(String ip) {
        closeAndReconnect();
        SERVER = ip;
        mHandler.removeCallbacks(mReconnectRunnable);
        mHandler.postDelayed(mReconnectRunnable, 100);
    }

    private Runnable mReconnectRunnable = new Runnable() {
        @Override
        public void run() {
            connect();
        }
    };

    public boolean isConnected() {
        return writer != null;
    }

    public void shutdown() {
        mHandler.post(()->disconnectFromServer());
    }

    public void sendMsg(String msg) {
        mHandler.post(()-> sendText(msg));
    }

    public void sendImg(Bitmap bmp) {
        mHandler.post(()->sendImgToServer(bmp));
    }

    private void connect() {
        if (mlistener != null) {
            mlistener.onNetworkStatusChanged();
        }
        try {
            // create a socket connection to the server
            socket = new Socket(SERVER, PORT);
            mHandler.removeCallbacks(mReconnectRunnable);
            // create a writer to send data to the server
            writer = new OutputStreamWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputstream = new DataInputStream(socket.getInputStream());
            Thread t = new Thread(()->readData(inputstream));
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
            closeAndReconnect();
        }
    }

    private void closeAndReconnect() {
        Log.d(TAG, "closeAndReconnect");
        try {
            if (socket != null) {
                socket.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
        writer = null;
        reader = null;
        mHandler.postDelayed(mReconnectRunnable, 500);
    }

    private void readData(DataInputStream input) {
        try {
            while(true) {
                byte messageType = input.readByte();
                if (messageType == 0x00) {//image
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    mlistener.onGotMsg(bitmap);
                } else if (messageType == 0x01){ //text
                    String text = input.readLine();
                    if (!"heartbeat".equals(text)) {
                        mlistener.onGotMsg(text);
                    }
                } else if (messageType == -1) {
                    Log.w(TAG, "disconnected, reconnect.");
                    break;
                }
            }
            closeAndReconnect();
        } catch (IOException e) {
            e.printStackTrace();
            closeAndReconnect();
        } finally {
            closeAndReconnect();
        }
    }

    private void sendImgToServer(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        sendDataToNetwork(0, data.length, data);
    }

    private void sendText(String text) {
        byte[] data = (text).getBytes(StandardCharsets.UTF_8);
        // write the message to the server with the header
        sendDataToNetwork(1, data.length, data);
    }

    private void sendDataToNetwork(int type, int length, byte[] data) {
        try {
            if (socket == null) return;
            OutputStream outputStream = socket.getOutputStream();

            outputStream.write(type);
            outputStream.write(ByteBuffer.allocate(4).putInt(data.length).array());
            outputStream.write(data);

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            closeAndReconnect();
            if (mlistener != null) {
                mlistener.onSendError();
            }
        }
    }

    public void disconnectFromServer() {
        try {
            // close the writer and the socket
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null) {
                socket.close();
            }
            writer = null;
            reader = null;
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable mShutdownRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "time to disconnect");
            disconnectFromServer();
        }
    };

    public void onGoingToBackground() {
        Log.d(TAG, "going to background, start timer");
        mHandler.postDelayed(mShutdownRunnable, 10*1000);//10min
    }

    public void onGoingToForeground() {
        Log.d(TAG, "going to foreground, connected = " + isConnected());
        mHandler.removeCallbacks(mShutdownRunnable);
        if (!isConnected()) {
            mHandler.post(mReconnectRunnable);
        }
    }
}
