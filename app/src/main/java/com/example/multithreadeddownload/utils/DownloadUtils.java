package com.example.multithreadeddownload.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * Created by 子非鱼 on 2018/2/22.
 */

public class DownloadUtils {
    private DownloadHttpTool mDownloadHttpTool;
    private OnDownloadListener onDownloadListener;

    private int fileSize;
    private int downloadedSize = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int length = msg.arg1;
            synchronized (this) {
                downloadedSize += length;
            }
            if (onDownloadListener != null) {
                onDownloadListener.downloadProgress(downloadedSize);
            }
            if (downloadedSize >= fileSize) {
                mDownloadHttpTool.complete();
                if (onDownloadListener != null) {
                    onDownloadListener.downloadEnd();
                }
            }
        }

    };

    public DownloadUtils(int threadCount, String filePath, String filename,
                         String urlString, Context context) {

        mDownloadHttpTool = new DownloadHttpTool(threadCount, urlString,
                filePath, filename, context, mHandler);
    }

    public void start() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... arg0) {
                mDownloadHttpTool.ready();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                fileSize = mDownloadHttpTool.getFileSize();
                downloadedSize = mDownloadHttpTool.getCompleteSize();
                Log.w("Tag", "downloadSize::" + downloadedSize);
                if (onDownloadListener != null) {
                    onDownloadListener.downloadStart(fileSize);
                }
                mDownloadHttpTool.start();
            }
        }.execute();
    }

    public void pause() {
        mDownloadHttpTool.pause();
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    //下载回调接口
    public interface OnDownloadListener {

        void downloadStart(int fileSize);

        void downloadProgress(int downloadedSize);//记录当前所有线程下总和

        void downloadEnd();
    }
}
