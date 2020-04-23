//package com.push.demo;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.webkit.ValueCallback;
//import android.webkit.WebChromeClient;
//
//
//public class UnicomWebView extends AdvancedWebView {
//
//    protected static final int REQUEST_CODE_CAMERA = 51427;
//    protected static final int REQUEST_CODE_FILE_PICKER = 51426;
//
//    protected String mFileTypes = "*/*";
//    protected Uri mCameraUri;
//
//    protected UnicomWebViewListener mUnicomWebViewListener;
//
//    public interface UnicomWebViewListener {
//        void onOpenFileInput(String accept, boolean allowMultiple);
//    }
//
//    public UnicomWebView(Context context) {
//        super(context);
//    }
//
//    public UnicomWebView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public UnicomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    public void setCameraUri(Uri cameraUri) {
//        mCameraUri = cameraUri;
//    }
//
//    public void setUnicomWebViewListener(UnicomWebViewListener listener) {
//        mUnicomWebViewListener = listener;
//    }
//
//    protected void openFileInput(ValueCallback<Uri> fileUploadCallbackFirst, ValueCallback<Uri[]> fileUploadCallbackSecond, String acceptType, String capture) {
//
//        android.util.Log.i(getClass().getSimpleName(), "openFileInput 1 " + acceptType + " " + capture);
//        if (mUnicomWebViewListener != null) {
//            mUnicomWebViewListener.onOpenFileInput(acceptType, true);
//        }
//    }
//
//    @SuppressLint("NewApi")
//    protected void openFileInput(ValueCallback<Uri> fileUploadCallbackFirst, ValueCallback<Uri[]> fileUploadCallbackSecond, WebChromeClient.FileChooserParams fileChooserParams) {
//        if (mFileUploadCallbackFirst != null) {
//            mFileUploadCallbackFirst.onReceiveValue(null);
//        }
//        mFileUploadCallbackFirst = fileUploadCallbackFirst;
//
//        if (mFileUploadCallbackSecond != null) {
//            mFileUploadCallbackSecond.onReceiveValue(null);
//        }
//        mFileUploadCallbackSecond = fileUploadCallbackSecond;
//
//
//        if (fileChooserParams != null) {
//            boolean allowMultiple = fileChooserParams.getMode() == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE;
//
//            String[] acceptTypes = fileChooserParams.getAcceptTypes();
//            Intent intent = fileChooserParams.createIntent();
//
//            String accept = mFileTypes;
//
//            if (acceptTypes != null && acceptTypes.length > 0) {
//                accept = acceptTypes[0];
//            }
//
//            android.util.Log.i(getClass().getSimpleName(), "openFileInput 2 " + accept + " " + intent);
//            if (mUnicomWebViewListener != null) {
//                mUnicomWebViewListener.onOpenFileInput(accept, allowMultiple);
//            }
//
//        }
//
//    }
//
//
//    @Override
//    public String getFileUploadPromptLabel() {
//        return super.getFileUploadPromptLabel();
//    }
//
//    @Override
//    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
//        if (requestCode == REQUEST_CODE_FILE_PICKER) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (intent != null) {
//                    if (mFileUploadCallbackFirst != null) {
//                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
//                        mFileUploadCallbackFirst = null;
//                    } else if (mFileUploadCallbackSecond != null) {
//                        Uri[] dataUris = null;
//
//                        try {
//                            if (intent.getDataString() != null) {
//                                dataUris = new Uri[]{Uri.parse(intent.getDataString())};
//                            } else {
//                                if (Build.VERSION.SDK_INT >= 16) {
//                                    if (intent.getClipData() != null) {
//                                        final int numSelectedFiles = intent.getClipData().getItemCount();
//
//                                        dataUris = new Uri[numSelectedFiles];
//
//                                        for (int i = 0; i < numSelectedFiles; i++) {
//                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
//                                        }
//                                    }
//                                }
//                            }
//                        } catch (Exception ignored) {
//                        }
//
//                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
//                        mFileUploadCallbackSecond = null;
//                    }
//                }
//            } else {
//                if (mFileUploadCallbackFirst != null) {
//                    mFileUploadCallbackFirst.onReceiveValue(null);
//                    mFileUploadCallbackFirst = null;
//                } else if (mFileUploadCallbackSecond != null) {
//                    mFileUploadCallbackSecond.onReceiveValue(null);
//                    mFileUploadCallbackSecond = null;
//                }
//            }
//        } else if (requestCode == REQUEST_CODE_CAMERA) {
//            if (resultCode == Activity.RESULT_OK && mCameraUri != null) {
//
//                Uri cameraUri = mCameraUri;
//                if (mFileUploadCallbackFirst != null) {
//                    mFileUploadCallbackFirst.onReceiveValue(cameraUri);
//                    mFileUploadCallbackFirst = null;
//                } else if (mFileUploadCallbackSecond != null) {
//                    Uri[] dataUris = new Uri[]{cameraUri};
//
//                    mFileUploadCallbackSecond.onReceiveValue(dataUris);
//                    mFileUploadCallbackSecond = null;
//                }
//
//            } else {
//                if (mFileUploadCallbackFirst != null) {
//                    mFileUploadCallbackFirst.onReceiveValue(null);
//                    mFileUploadCallbackFirst = null;
//                } else if (mFileUploadCallbackSecond != null) {
//                    mFileUploadCallbackSecond.onReceiveValue(null);
//                    mFileUploadCallbackSecond = null;
//                }
//            }
//        }
//    }
//
//
//}
