package com.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class HttpUtil {
    private static final Long CONN_TIME_OUT = 30000L;
    private static final Long READ_TIME_OUT = 30000L;
    private static final String TAG = "HttpUtil";
    private static OkHttpClient sHttpClient = new OkHttpClient.Builder().connectTimeout(CONN_TIME_OUT, TimeUnit.MILLISECONDS).readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS).build();


    public static void downloadFileByOkio(String url, File destFile) {

        BufferedSink sink = null;
        BufferedSource source = null;
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = sHttpClient.newCall(request).execute();
            ResponseBody body = response.body();
            long contentLength = body.contentLength();
            source = body.source();
            sink = Okio.buffer(Okio.sink(destFile));
            Buffer sinkBuffer = sink.buffer();
            long totalBytesRead = 0;
            int bufferSize = 8 * 1024;
            long bytesRead;
            while ((bytesRead = source.read(sinkBuffer, bufferSize)) != -1) {
                sink.emit();
                totalBytesRead += bytesRead;
                int progress = (int) ((totalBytesRead * 100) / contentLength);
            }
            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(sink);
            Util.closeQuietly(source);
        }

    }



    public static void main(String[] args){

    }
}