package com.bondex.ysl.databaselibrary.buinesslog;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * date: 2020/5/22
 *
 * @Author: ysl
 * description:
 */
@Database(entities = BusinessLogBean.class, version = 1, exportSchema = false)
public abstract class BusinessLogDataBase extends RoomDatabase {

    private final static String DB_NAME = "business_log";

    private static BusinessLogDataBase instance;

    public static BusinessLogDataBase getInstance(Context context) {

        if (instance == null) {
            instance = create(context);
        }

        return instance;
    }

    private static BusinessLogDataBase create(Context context) {

        return Room.databaseBuilder(context, BusinessLogDataBase.class, DB_NAME).build();
    }

    public abstract BusinessLogDao getDao();

}
