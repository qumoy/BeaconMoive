package com.beacon.moive.Dbs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author Qumoy
 * Create Date 2020/2/3
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class SqlHelper extends SQLiteOpenHelper {
    public SqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table Moive(_id integer primary key autoincrement,minor integer,name varchar(20),time varchar(20),actor varchar(40),type varchar(40),pic varchar(255),description varchar(255),post varchar(2000))");
        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX nn ON Moive (minor)");
        sqLiteDatabase.execSQL("create table User(_id integer primary key autoincrement,name varchar(20),password varchar(20),loginTime varchar(40),registerTime varchar(40),isUser Integer)");
        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX mm ON User (name)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
