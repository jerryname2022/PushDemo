package com.unicom.web;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.push.demo.PermissionHelper;
import com.push.demo.R;
import com.tencent.smtt.sdk.WebSettings;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class UnicomWebActivity extends Activity {
    public static final String TAG = UnicomWebActivity.class.getSimpleName();

    List<String> mImageAccepts = new ArrayList<>();

    RelativeLayout mWebRl;
    UnicomX5WebView mWebContentWv;
    String mURL;

    CameraDialog mCameraDialog;
    boolean mAllowMultiple;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_layout);
        mWebRl = findViewById(R.id.web_rl);

        mImageAccepts.clear();
        mImageAccepts.add("*/*");
        mImageAccepts.add("image/*");

        mCameraDialog = new CameraDialog(this, new CameraDialog.OnCameraDialogListener() {

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
                mWebContentWv.onActivityResult(UnicomX5WebView.REQUEST_CODE_CAMERA, 0, null);
            }

            @Override
            public void onCamera(Dialog dialog) {

                boolean permission = PermissionHelper.checkDCIMPermission(UnicomWebActivity.this);
                if (permission) {
                    dialog.dismiss();
                    cameraAction();
                } else {
                    PermissionHelper.requestDCIMPermissions(UnicomWebActivity.this, PermissionHelper.CODE_REQUEST_DCIM);
                }
            }

            @Override
            public void onSdcard(Dialog dialog) {
                dialog.dismiss();
                try {
                    galleryAction();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        mWebContentWv = new UnicomX5WebView(this);
        ViewGroup.LayoutParams layoutParams = mWebContentWv.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mWebContentWv.setLayoutParams(layoutParams);
        mWebRl.addView(mWebContentWv);

        mURL = getIntent().getStringExtra("URL");
        initView();

        mWebContentWv.setUnicomWebViewListener(new UnicomX5WebView.UnicomWebViewListener() {
            @Override
            public void onOpenFileInput(String accept, boolean allowMultiple) {
                if (mCameraDialog != null && mImageAccepts.contains(accept)) {
                    mAllowMultiple = allowMultiple;
                    mCameraDialog.show();
                }
            }
        });
    }


    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void initView() {
        com.tencent.smtt.sdk.WebSettings webSettings = mWebContentWv.getSettings();
        // 不支持缩放
        webSettings.setSupportZoom(false);
        // 自适应屏幕大小
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //使用缓存
        webSettings.setAppCacheEnabled(true);
        //DOM Storage
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //启动对js的支持
        webSettings.setJavaScriptEnabled(true);
        //对图片大小适配
        webSettings.setUseWideViewPort(true);
        //对文字大小适配
        webSettings.setLoadWithOverviewMode(true);
        // 判断系统版本是不是5.0或之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //让系统不屏蔽混合内容和第三方Cookie
            // CookieManager.getInstance().setAcceptThirdPartyCookies(mWebContentWv, true);
            // webSettings.setMixedContentMode(0);//永远允许
        }

        com.tencent.smtt.sdk.WebChromeClient wvcc = new com.tencent.smtt.sdk.WebChromeClient() {
            @Override
            public boolean onCreateWindow(com.tencent.smtt.sdk.WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

            @Override
            public void onReceivedTitle(com.tencent.smtt.sdk.WebView view, String title) {
                super.onReceivedTitle(view, title);
                // mNormalTitleTitleTv.setText(title);
            }

            // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(com.tencent.smtt.sdk.ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, null);
            }

            // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
            public void openFileChooser(com.tencent.smtt.sdk.ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, null);
            }

            // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(com.tencent.smtt.sdk.ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mWebContentWv.openFileInput(uploadMsg, null, acceptType, capture);
            }

            @Override
            public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView webView, com.tencent.smtt.sdk.ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {

                if (Build.VERSION.SDK_INT >= 21) {
                    mWebContentWv.openFileInput(null, valueCallback, fileChooserParams);
                    return true;
                } else {
                    return false;
                }
                // return super.onShowFileChooser(webView, valueCallback, fileChooserParams);
            }

        };

        // mWebContentWv.set
        // 设置setWebChromeClient对象
        mWebContentWv.setWebChromeClient(wvcc);
        mWebContentWv.addJavascriptInterface(this, "JS_ANDROID");

        // 创建WebViewClient对象
        com.tencent.smtt.sdk.WebViewClient wvc = new com.tencent.smtt.sdk.WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                mWebContentWv.loadUrl(url);
                // 消耗掉这个事件。Android中返回True的即到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
                return false;
            }

            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
                super.onPageFinished(view, url);
                // 获取页面内容
                view.loadUrl("javascript:window.JS_ANDROID.onHtmlLoaded(\"" + url + "\", "
                        + "document.getElementsByTagName('html')[0].innerHTML);");
            }

            @Override
            public void onReceivedError(com.tencent.smtt.sdk.WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
            }

        };

        mWebContentWv.setWebViewClient(wvc);
        mWebContentWv.loadUrl(mURL);
    }


    @Override
    protected void onDestroy() {
        mWebContentWv.destroy();
        mWebContentWv = null;
        super.onDestroy();
    }


    public void galleryAction() throws UnsupportedEncodingException {

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);

        if (mAllowMultiple) {
            if (Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, UnicomX5WebView.decodeBase64("6YCJ5oup5LiA5Liq5paH5Lu2")), UnicomX5WebView.REQUEST_CODE_FILE_PICKER);
    }


    private void cameraAction() {

        try {
            String name = new SimpleDateFormat("yyyyMMddhhmmss").format(System.currentTimeMillis()) + ".jpg";
            File file = new File(Environment.getExternalStorageDirectory(), name);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri cameraUri = FileProvider.getUriForFile(this, "com.unicom.web.fileprovider", file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            startActivityForResult(cameraIntent, UnicomX5WebView.REQUEST_CODE_CAMERA);
            mWebContentWv.setCameraUri(cameraUri);
        } catch (Exception e) {
            e.printStackTrace();
            makeText("打开摄像头失败");
        }
    }


    Toast mToast;

    public void makeText(String msg) {
        if (TextUtils.isEmpty(msg)) return;
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebContentWv.onActivityResult(requestCode, resultCode, intent);
    }

    @JavascriptInterface
    public void onHtmlLoaded(String url, String html) {
        Log.i(getClass().getSimpleName(), "onHtmlLoaded " + url);
    }

    @JavascriptInterface
    public void CUSCcallback() {
        Log.i(getClass().getSimpleName(), "====>CUSCcallback=");
        finish();
    }


}
