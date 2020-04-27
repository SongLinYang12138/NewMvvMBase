package com.bondex.ysl.databaselibrary.login;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * date: 2019/7/19
 * Author: ysl
 * description:
 */
@Dao
public interface LoginDao {

    @Insert
    void insert(LoginBean bean);

    @Update
    void update(LoginBean bean);

    @Query("SELECT * FROM LoginBean WHERE psncode in (:code)")
    LoginBean check(String code);

    @Query("SELECT * FROM LoginBean WHERE (psnname LIKE '%' || :name || '%')")
    List<LoginBean> selectByName(String name);

    @Query("SELECT * FROM LoginBean ORDER BY loginDate DESC")
    List<LoginBean> getByLoginDate();


}
