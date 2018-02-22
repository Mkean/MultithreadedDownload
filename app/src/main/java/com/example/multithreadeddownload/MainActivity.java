package com.example.multithreadeddownload;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.multithreadeddownload.utils.DownloadUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private ProgressBar mProgress;
    private TextView mTotal;
    private Button mPause;
    private Button mStart;
    private DownloadUtils mDownloadUtils;
    private int threadCount = 10;
    private int Max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

    }

    private void setListener() {
        mStart.setOnClickListener(this);
        mPause.setOnClickListener(this);
    }

    private void initData() {
        String url = "http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4";
        final String localPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/local";
        final String fileName = "abc.mp4";
        Log.e(TAG, localPath + "/" + fileName);
        mDownloadUtils = new DownloadUtils(threadCount, localPath, fileName, url, this);
        mDownloadUtils.setOnDownloadListener(new DownloadUtils.OnDownloadListener() {
            @Override
            public void downloadStart(int fileSize) {
                Max = fileSize;
                mProgress.setMax(fileSize);
            }

            @Override
            public void downloadProgress(int downloadedSize) {
                Log.e("tag", "Complete::" + downloadedSize);
                mProgress.setProgress(downloadedSize);
                mTotal.setText("下载完成" + (int) downloadedSize * 100 / Max + "%");
            }

            @Override
            public void downloadEnd() {
                mProgress.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "下载完成！", Toast.LENGTH_SHORT).show();
            }
        });

        setListener();
    }

    private void initView() {
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mTotal = (TextView) findViewById(R.id.total);
        mStart = (Button) findViewById(R.id.start);
        mPause = (Button) findViewById(R.id.pause);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mDownloadUtils.start();
                break;
            case R.id.pause:
                mDownloadUtils.pause();
                break;
            default:
                break;
        }
    }
}
