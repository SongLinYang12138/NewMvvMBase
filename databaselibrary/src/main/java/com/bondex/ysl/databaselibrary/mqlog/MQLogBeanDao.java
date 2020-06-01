package com.bondex.ysl.databaselibrary.mqlog;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * date: 2020/5/22
 *
 * @Author: ysl
 * description:
 */
@Dao
public interface MQLogBeanDao {

    @Insert
    void insert(MQLogBean logBean);

    @Delete
    void delete(MQLogBean logBean);

    @Query("DELETE from MQLogBean WHERE time_id <= (:deleteTime)")
    void deleteBefor(long deleteTime);

    @Query("SELECT * FROM MQLogBean order by time_id desc")
    List<MQLogBean> getAll();
}
