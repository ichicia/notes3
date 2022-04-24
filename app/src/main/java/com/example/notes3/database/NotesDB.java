package com.example.notes3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by flyan on 6/19/18.
 */

public class NotesDB extends SQLiteOpenHelper {
    public final static String DB_NAME = "notes.db";
    public final static int DB_VERSION = 2;

    public static String currUser = "";

    //数据库里需要的东西
    public final static String TABLE_NAME = "notes";
    public final static String ID = "_id";
    public final static String CONTENT = "content";
    public final static String PATH = "path";
    public final static String VIDEO = "video";
    public final static String TIME = "time";
    public final static String USER = "user";
    public final static String PLAN_TIME = "plan_time";
    /**
     * 便签是否完成
     * 1：未完成
     * 7：完成
     */
    public final static String OK = "ok";

    //用户表
    public final static String TABLE_USER = "user";
    public final static String USER_ID = "id";
    public final static String ACCOUNT = "account";
    public final static String PS = "ps";

    public NotesDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSQL = "create table " + TABLE_NAME + "(" +
                ID + " integer primary key autoincrement," +
                CONTENT + " text," +
                PATH + " text," +
                VIDEO + " text," +
                USER + " text," +
                PLAN_TIME + " text," +
                TIME + " text," +
                OK + " integer" +
                ")";

        String createSQL1 = "create table " + TABLE_USER + "(" +
                USER_ID + " integer primary key autoincrement," +
                ACCOUNT + " text," +
                PS + " text" +
                ")";

        db.execSQL(createSQL);
        db.execSQL(createSQL1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
