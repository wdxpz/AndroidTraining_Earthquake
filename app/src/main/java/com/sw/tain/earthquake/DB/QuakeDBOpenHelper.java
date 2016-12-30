package com.sw.tain.earthquake.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sw.tain.earthquake.DB.QuakeDBModel.*;

/**
 * Created by home on 2016/12/30.
 */

public class QuakeDBOpenHelper extends SQLiteOpenHelper {
   private static final String TAG = "QuakeDBOpenHelper";

    private static final String DATABSE_NAME = "quake.db";
    private static final int VERSION = 1;

    private static final String SQL_Create_Table = "create table " + QuakeTable.NAME + "("
            + QuakeTable.COL.KEY_ID + " integer primary key autoincrement, "
            + QuakeTable.COL.DATE + " long, "
            + QuakeTable.COL.DETAILS + " text, "
            + QuakeTable.COL.SUMMARY + " text, "
            + QuakeTable.COL.LATITUDE + " float,"
            + QuakeTable.COL.LONGITUDE + " float,"
            + QuakeTable.COL.MAGNITUDE + " float,"
            + QuakeTable.COL.LINK + " text);";


    public QuakeDBOpenHelper(Context context) {
        super(context, DATABSE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_Create_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from " + oldVersion + "to " + newVersion
                + ", which will destroy old data");
        db.execSQL("drop table if exists " + QuakeTable.NAME);
        onCreate(db);
    }
}
