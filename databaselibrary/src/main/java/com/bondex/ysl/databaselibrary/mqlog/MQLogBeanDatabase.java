package com.bondex.ysl.databaselibrary.mqlog;

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
@Database(entities = MQLogBean.class, version = 1, exportSchema = false)
public abstract class MQLogBeanDatabase extends RoomDatabase {

    private static final String DB_NAME = "mq_log";

    private static MQLogBeanDatabase instance;


    public static MQLogBeanDatabase getInstance(Context context) {

        if (instance == null) {
            instance = create(context);
        }

        return instance;
    }

    private static MQLogBeanDatabase create(Context context) {

        return Room.databaseBuilder(context, MQLogBeanDatabase.class, DB_NAME).build();
    }

    public abstract MQLogBeanDao getDao();
}
