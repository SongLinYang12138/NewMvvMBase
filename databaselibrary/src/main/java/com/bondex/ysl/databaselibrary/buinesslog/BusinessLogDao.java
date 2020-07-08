package com.bondex.ysl.databaselibrary.buinesslog;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * date: 2020/5/22
 *
 * @Author: ysl
 * description:
 */
@Dao
public interface BusinessLogDao {

    @Insert
    long insert(BusinessLogBean logBean);

    @Delete
    int delete(BusinessLogBean logBean);

    @Update
    int update(BusinessLogBean logBean);

    @Query("SELECT * FROM BusinessLogBean WHERE seqno in (:seqNo)")
    BusinessLogBean selectBySeqNo(String seqNo);

    @Query("SELECT * FROM BUSINESSLOGBEAN WHERE create_time <= (:time)")
    List<BusinessLogBean> selectBeforeTime(long time);

    @Query("SELECT * FROM BUSINESSLOGBEAN WHERE status != 0 ORDER BY create_time DESC")
    List<BusinessLogBean> selectNoSuccess();


    @Query("SELECT * FROM BUSINESSLOGBEAN")
    List<BusinessLogBean> getAll();

    @Query("DELETE FROM BUSINESSLOGBEAN WHERE create_time <= (:time) and status == 0")
   void deleteSuccessBefore(long time);
    @Delete
    int deleteLists(List<BusinessLogBean> list);

    @Query("DELETE FROM BusinessLogBean WHERE status == 0 and create_time <= (:time)")
    int deletSuccessBeforTime(long time);

    @Query("SELECT * FROM BUSINESSLOGBEAN WHERE status == 0 and create_time <= (:lastWeek)")
    List<BusinessLogBean> deleteLastWeek(long lastWeek);
}
