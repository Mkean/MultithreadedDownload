package com.example.multithreadeddownload.utils;

import android.util.Log;

import com.example.multithreadeddownload.bean.DownloadInfo;
import com.example.multithreadeddownload.bean.GreenDaoBean;
import com.example.multithreadeddownload.database.GreenDaoBeanDao;

import java.util.ArrayList;
import java.util.List;

import static com.example.multithreadeddownload.app.MyApp.dao;

/**
 * Created by 子非鱼 on 2018/2/22.
 */

public class DownloadSqlTool {
    /**
     * 创建下载的具体信息
     */
    public void insertInfo(List<DownloadInfo> infos) {
        for (DownloadInfo info : infos) {
            GreenDaoBean user = new GreenDaoBean(null, info.getThreadId(), info.getStartPos(), info.getEndPos(), info.getCompleteSize(), info.getUrl());
            dao.insert(user);
        }
    }

    /**
     * 得到下载具体信息
     */
    public List<DownloadInfo> getInfo(String urlStr) {
        List<DownloadInfo> list = new ArrayList<>();
        List<GreenDaoBean> list1 = dao.queryBuilder().where(GreenDaoBeanDao.Properties.Url.eq(urlStr)).build().list();
        for (GreenDaoBean user : list1) {
            DownloadInfo info = new DownloadInfo(
                    user.getThread_id(), user.getStart_pos(), user.getEnd_pos(),
                    user.getComplete_size(), user.getUrl());
            Log.d("main-----", info.toString());
            list.add(info);
        }

        return list;
    }

    /**
     * 更新数据库中的下载信息
     */
    public void updateInfo(int threadId, int completeSize, String urlStr) {
        GreenDaoBean user = dao.queryBuilder()
                .where(GreenDaoBeanDao.Properties.Thread_id.eq(threadId), GreenDaoBeanDao.Properties.Url.eq(urlStr)).build().unique();
        user.setComplete_size(completeSize);
        dao.update(user);
    }

    /**
     * 下载完成后删除数据库中的数据
     */
    public void delete() {
        dao.deleteAll();
    }
}
