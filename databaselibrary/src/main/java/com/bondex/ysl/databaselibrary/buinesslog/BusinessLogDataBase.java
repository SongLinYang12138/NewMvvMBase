package com.bondex.ysl.databaselibrary.buinesslog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * date: 2020/5/22
 *
 * @Author: ysl
 * description:
 */
@Database(entities = BusinessLogBean.class, version = 2)
public abstract class BusinessLogDataBase extends RoomDatabase {

    private final static String DB_NAME = "business_log";

    private static BusinessLogDataBase instance;

    private static final Migration MIGRATION_ADD_QRCODE_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'BusinessLogBean' ADD COLUMN 'qrcode' TEXT" );
        }
    };


    public static BusinessLogDataBase getInstance(Context context) {

        if (instance == null) {
            instance = create(context);
        }

        return instance;
    }

    private static BusinessLogDataBase create(Context context) {

        return Room.databaseBuilder(context, BusinessLogDataBase.class, DB_NAME)
                .addMigrations(MIGRATION_ADD_QRCODE_1_2)
                .build();
    }

    public abstract BusinessLogDao getDao();

}
