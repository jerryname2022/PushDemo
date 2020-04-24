package com.unicom.web;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.push.demo.R;


public class CameraDialog extends Dialog implements DialogInterface.OnShowListener, View.OnClickListener {

    TextView mDialogCameraSdcardTv;
    TextView mDialogCameraCameraTv;
    TextView mDialogCameraCancelTv;
    LinearLayout mDialogCameraRootLl;

    OnCameraDialogListener mListener;

    public CameraDialog(Context context, OnCameraDialogListener listener) {
        super(context);
        setOnShowListener(this);
        setContentView(R.layout.dialog_camera_layout);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        this.mListener = listener;


        mDialogCameraSdcardTv = findViewById(R.id.dialog_camera_sdcard_tv);
        mDialogCameraCameraTv = findViewById(R.id.dialog_camera_camera_tv);
        mDialogCameraCancelTv = findViewById(R.id.dialog_camera_cancel_tv);
        mDialogCameraRootLl = findViewById(R.id.dialog_camera_root_ll);

        mDialogCameraSdcardTv.setOnClickListener(this);
        mDialogCameraCameraTv.setOnClickListener(this);
        mDialogCameraCancelTv.setOnClickListener(this);
    }


    @Override
    public void onShow(DialogInterface dialog) {
        final Window win = getWindow();
        win.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = win.getAttributes();
//        lp.width = mDialogCameraRootLl.getWidth();
//        lp.height = mDialogCameraRootLl.getHeight();
//        win.setAttributes(lp);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_camera_sdcard_tv:
                if (mListener != null) {
                    mListener.onSdcard(this);
                }
                break;
            case R.id.dialog_camera_camera_tv:
                if (mListener != null) {
                    mListener.onCamera(this);
                }
                break;
            case R.id.dialog_camera_cancel_tv:
                if (mListener != null) {
                    mListener.onCancel(this);
                }
                break;
        }
    }


    public interface OnCameraDialogListener {
        void onCancel(Dialog dialog);

        void onCamera(Dialog dialog);

        void onSdcard(Dialog dialog);
    }

}
