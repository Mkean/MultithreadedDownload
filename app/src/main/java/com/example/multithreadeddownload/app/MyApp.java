package com.example.multithreadeddownload.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.example.multithreadeddownload.database.DaoMaster;
import com.example.multithreadeddownload.database.DaoSession;
import com.example.multithreadeddownload.database.GreenDaoBeanDao;

import org.greenrobot.greendao.database.Database;


/**
 * Created by 子非鱼 on 2018/2/22.
 */

public class MyApp extends Application {

    public static GreenDaoBeanDao dao;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "sequel.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        dao = daoSession.getGreenDaoBeanDao();
    }
}
