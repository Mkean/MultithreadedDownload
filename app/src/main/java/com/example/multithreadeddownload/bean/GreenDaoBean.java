package com.example.multithreadeddownload.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.example.multithreadeddownload.database.DaoSession;
import com.example.multithreadeddownload.database.GreenDaoBeanDao;

/**
 * Created by 子非鱼 on 2018/2/22.
 */
@Entity(active = true)
public class GreenDaoBean {
    @Id
    private Long id;
    private Integer thread_id;
    private Integer start_pos;
    private Integer end_pos;
    private Integer complete_size;
    private String url;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1499311143)
    private transient GreenDaoBeanDao myDao;
    @Generated(hash = 9616199)
    public GreenDaoBean(Long id, Integer thread_id, Integer start_pos,
            Integer end_pos, Integer complete_size, String url) {
        this.id = id;
        this.thread_id = thread_id;
        this.start_pos = start_pos;
        this.end_pos = end_pos;
        this.complete_size = complete_size;
        this.url = url;
    }
    @Generated(hash = 826843181)
    public GreenDaoBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getThread_id() {
        return this.thread_id;
    }
    public void setThread_id(Integer thread_id) {
        this.thread_id = thread_id;
    }
    public Integer getStart_pos() {
        return this.start_pos;
    }
    public void setStart_pos(Integer start_pos) {
        this.start_pos = start_pos;
    }
    public Integer getEnd_pos() {
        return this.end_pos;
    }
    public void setEnd_pos(Integer end_pos) {
        this.end_pos = end_pos;
    }
    public Integer getComplete_size() {
        return this.complete_size;
    }
    public void setComplete_size(Integer complete_size) {
        this.complete_size = complete_size;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1989982368)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGreenDaoBeanDao() : null;
    }

}
