package com.push.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

/**
 * Created by walle on 2019/3/20.
 */

public class MyApplication extends Application {

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("MyApplication", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);


        initUmengSDK();
//        initUmengSDKDelay();
    }

    private void initUmengSDK() {
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(mContext, "5e0ee342cb23d28dbf00006c", "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "615091ff4d83e369993ecfd222bbe139");

        PushAgent.getInstance(this).register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                Log.i("walle", "--->>> onSuccess, s is " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i("walle", "--->>> onFailure, s is " + s + ", s1 is " + s1);
            }
        });
    }

    private void initUmengSDKDelay() {
        if (getApplicationContext().getPackageName().equals(getCurrentProcessName())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initUmengSDK();
                }
            }, 5000);
        } else {
            initUmengSDK();
        }
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }


}
