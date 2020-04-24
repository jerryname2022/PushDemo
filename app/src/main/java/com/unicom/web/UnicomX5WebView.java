package com.unicom.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Base64;

import com.tencent.smtt.sdk.WebChromeClient;

import java.io.UnsupportedEncodingException;


public class UnicomX5WebView extends com.tencent.smtt.sdk.WebView {

    protected static final int REQUEST_CODE_CAMERA = 51427;
    protected static final int REQUEST_CODE_FILE_PICKER = 51426;

    protected String mFileTypes = "*/*";
    protected Uri mCameraUri;

    /**
     * File upload callback for platform versions prior to Android 5.0
     */
    protected com.tencent.smtt.sdk.ValueCallback<Uri> mFileUploadCallbackFirst;
    /**
     * File upload callback for Android 5.0+
     */
    protected com.tencent.smtt.sdk.ValueCallback<Uri[]> mFileUploadCallbackSecond;
    protected UnicomWebViewListener mUnicomWebViewListener;

    public interface UnicomWebViewListener {
        void onOpenFileInput(String accept, boolean allowMultiple);
    }


    public UnicomX5WebView(Context context) {
        super(context);
    }

    public UnicomX5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnicomX5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCameraUri(Uri cameraUri) {
        mCameraUri = cameraUri;
    }

    public void setUnicomWebViewListener(UnicomWebViewListener listener) {
        mUnicomWebViewListener = listener;
    }

    public void openFileInput(com.tencent.smtt.sdk.ValueCallback<Uri> fileUploadCallbackFirst, com.tencent.smtt.sdk.ValueCallback<Uri[]> fileUploadCallbackSecond, String acceptType, String capture) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        if (mUnicomWebViewListener != null) {
            mUnicomWebViewListener.onOpenFileInput(acceptType, true);
        }
    }

    public static String decodeBase64(final String base64) throws IllegalArgumentException, UnsupportedEncodingException {
        final byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return new String(bytes, "UTF-8");
    }

    @SuppressLint("NewApi")
    public void openFileInput(com.tencent.smtt.sdk.ValueCallback<Uri> fileUploadCallbackFirst, com.tencent.smtt.sdk.ValueCallback<Uri[]> fileUploadCallbackSecond, com.tencent.smtt.sdk.WebChromeClient.FileChooserParams fileChooserParams) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;


        if (fileChooserParams != null) {
            boolean allowMultiple = fileChooserParams.getMode() == com.tencent.smtt.sdk.WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE;

            String[] acceptTypes = fileChooserParams.getAcceptTypes();
            String accept = mFileTypes;
            if (acceptTypes != null && acceptTypes.length > 0) {
                accept = acceptTypes[0];
            }

            if (mUnicomWebViewListener != null) {
                mUnicomWebViewListener.onOpenFileInput(accept, allowMultiple);
            }

        }

    }


    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == REQUEST_CODE_FILE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris = null;

                        try {
                            if (intent.getDataString() != null) {
                                dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                            } else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.getClipData() != null) {
                                        final int numSelectedFiles = intent.getClipData().getItemCount();

                                        dataUris = new Uri[numSelectedFiles];

                                        for (int i = 0; i < numSelectedFiles; i++) {
                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK && mCameraUri != null) {

                Uri cameraUri = mCameraUri;
                if (mFileUploadCallbackFirst != null) {

                    mFileUploadCallbackFirst.onReceiveValue(cameraUri);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    Uri[] dataUris = new Uri[]{cameraUri};

                    mFileUploadCallbackSecond.onReceiveValue(dataUris);
                    mFileUploadCallbackSecond = null;
                }

            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }


}
