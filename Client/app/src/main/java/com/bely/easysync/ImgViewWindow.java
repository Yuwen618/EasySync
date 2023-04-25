package com.bely.easysync;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImgViewWindow extends PopupWindow {
    private View contentView;

    public ImgViewWindow(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.activity_img_view, null);
        setContentView(contentView);
        // 设置宽度和高度
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        // 设置背景为透明
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置弹出动画
        setAnimationStyle(R.style.PopupAnimation);

        ImageView mImgView = contentView.findViewById(R.id.imageView);
        mImgView.setImageBitmap(Utils.getBitmapClicked());

        ImageView btnsave = contentView.findViewById(R.id.savebutton);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.saveImg(Utils.getBitmapClicked());
            }
        });

        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).exitFulLScreen();
                dismiss();
            }
        });

        ((ScaleView)mImgView).setHostListener(new ScaleView.HostListener() {
            @Override
            public void onSwipeUpDown(boolean down) {
                ImgViewWindow.this.setAnimationStyle(down ? R.style.PopupAnimation : R.style.PopupAnimation2);
                ((MainActivity)context).exitFulLScreen();
                dismiss();
            }
        });
    }


}
