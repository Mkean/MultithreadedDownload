package com.example.multithreadeddownload.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.multithreadeddownload.bean.DownloadInfo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 子非鱼 on 2018/2/22.
 */

public class DownloadHttpTool {
    private static final String TAG = DownloadHttpTool.class.getSimpleName();
    private int threadCount;//线程数量
    private String urlStr;//URL地址
    private Context mContext;
    private Handler mHandler;
    private List<DownloadInfo> downloadInfos;//保存下载信息的类

    private String localPath;//目录
    private String fileName;//文件名
    private int fileSize;
    private DownloadSqlTool sqlTool;//文件信息保存的数据库操作类

    private enum Download_State {
        Downloading, Pause, Ready;//利用枚举表示下载的三种状态
    }

    private Download_State state = Download_State.Ready;//当前下载状态

    private int globalComplete = 0;//所有线程下载的总数

    public DownloadHttpTool(int threadCount, String urlString,
                            String localPath, String fileName, Context context, Handler handler) {
        super();
        this.threadCount = threadCount;
        this.urlStr = urlString;
        this.localPath = localPath;
        this.mContext = context;
        this.mHandler = handler;
        this.fileName = fileName;
        sqlTool = new DownloadSqlTool();
    }

    //在开始下载之前需要调用ready方法进行配置
    public void ready() {
        Log.w(TAG, "ready");
        globalComplete = 0;
        downloadInfos = sqlTool.getInfo(urlStr);
        if (downloadInfos.size() == 0) {
            initFirst();
        } else {
            File file = new File(localPath + "/" + fileName);

            if (!file.exists()) {//文件不存在
                sqlTool.delete();
                initFirst();
            } else {//文件存在
                fileSize = downloadInfos.get(downloadInfos.size() - 1)
                        .getEndPos();
                for (DownloadInfo info : downloadInfos) {
                    globalComplete += info.getCompleteSize();
                }
                Log.w(TAG, "globalComplete:::" + globalComplete + ":::fileSize:::" + fileSize);
            }
        }
    }

    public void start() {
        Log.w(TAG, "start");
        if (downloadInfos != null) {
            if (state == Download_State.Downloading) {
                return;
            }
            state = Download_State.Downloading;
            for (DownloadInfo info : downloadInfos) {
                Log.v(TAG, "startThread");
                new DownloadThread(info.getThreadId(), info.getStartPos(),
                        info.getEndPos(), info.getCompleteSize(),
                        info.getUrl()).start();
            }
        }
    }

    public void pause() {
        state = Download_State.Pause;
    }

    public void delete() {
        complete();
        File file = new File(localPath + "/" + fileName);
        file.delete();
    }

    public void complete() {
        sqlTool.delete();
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getCompleteSize() {
        return globalComplete;
    }

    //第一次下载初始化
    private void initFirst() {
        Log.w(TAG, "initFirst");
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            //文件长度
            fileSize = connection.getContentLength();
            Log.w(TAG, "fileSize::" + fileSize);
            File fileParent = new File(localPath);
            if (!fileParent.exists()) {
                fileParent.mkdir();
            }
            File file = new File(fileParent, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 本地访问文件
            RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
            accessFile.setLength(fileSize);
            accessFile.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int range = fileSize / threadCount;

        downloadInfos = new ArrayList<>();
        for (int i = 0; i < threadCount - 1; i++) {
            DownloadInfo info = new DownloadInfo(i, i * range, (i + 1) * range
                    - 1, 0, urlStr);
            downloadInfos.add(info);
        }
        DownloadInfo info = new DownloadInfo(threadCount - 1, (threadCount - 1)
                * range, fileSize - 1, 0, urlStr);
        downloadInfos.add(info);
        sqlTool.insertInfo(downloadInfos);
    }

    //自定义下载线程
    private class DownloadThread extends Thread {

        private int threadId;
        private int startPos;
        private int endPos;
        private int completeSize;
        private String urlStr;
        private int totalThreadSize;//每个线程下载的文件长度

        public DownloadThread(int threadId, int startPos, int endPos,
                              int completeSize, String urlStr) {
            this.threadId = threadId;
            this.startPos = startPos;
            this.endPos = endPos;

            this.urlStr = urlStr;
            this.completeSize = completeSize;
            totalThreadSize = endPos - startPos + 1;
        }

        @Override
        public void run() {

            HttpURLConnection connection = null;
            RandomAccessFile randomAccessFile = null;
            InputStream is = null;
            try {
                randomAccessFile = new RandomAccessFile(localPath + "/"
                        + fileName, "rwd");
                randomAccessFile.seek(startPos + completeSize);


                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                //支持断点续传，指定范围
                connection.setRequestProperty("Range", "bytes="
                        + (startPos + completeSize) + "-" + endPos);
                is = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, length);
                    completeSize += length;
                    Message message = Message.obtain();
                    message.what = threadId;
                    message.obj = urlStr;
                    message.arg1 = length;
                    Log.e("TAG", length + "::" + threadId);
                    mHandler.sendMessage(message);
                    sqlTool.updateInfo(threadId, completeSize, urlStr);
                    Log.w(TAG, "ThreadId::" + threadId + "    complete::"
                            + completeSize + "    total::" + totalThreadSize);

                    if (completeSize >= totalThreadSize) {
                        break;
                    }
                    if (state != Download_State.Downloading) {
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    randomAccessFile.close();
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
