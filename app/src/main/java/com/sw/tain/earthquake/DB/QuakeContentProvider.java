package com.sw.tain.earthquake.DB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sw.tain.earthquake.DB.QuakeDBModel.*;
/**
 * Created by home on 2016/12/30.
 */

public class QuakeContentProvider extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("content://com.sw.tain.earthquake.provider/elements");
    private static final int SINGLE_ROW = 1;
    private static final int ALL_ROW = 2;

    private static final UriMatcher mUriMatcher;

    static{
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI("com.sw.tain.earthquake.provider", "elements", ALL_ROW);
        mUriMatcher.addURI("com.sw.tain.earthquake.provider", "elements/#", SINGLE_ROW);
    }

    private QuakeDBOpenHelper mDBOpenHelper;
    private SQLiteDatabase mDatabase;

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)){
            case SINGLE_ROW: return "vnd.android.cursor.dir/vnd.com.sw.tain.earthquake";
            case ALL_ROW: return "vnd.android.cursor.item/vnd.com.sw.tain.earthquake";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public boolean onCreate() {

        mDBOpenHelper = new QuakeDBOpenHelper(getContext());
        mDatabase = mDBOpenHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(QuakeTable.NAME);

        switch (mUriMatcher.match(uri)){
            case SINGLE_ROW:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(QuakeTable.COL.KEY_ID + "=" + id);
                break;
            default:
                break;
        }

        String orderBy;
        if(TextUtils.isEmpty(sortOrder)){
            orderBy = QuakeTable.COL.DATE;
        }else{
            orderBy = sortOrder;
        }

        Cursor cursor = queryBuilder.query(mDatabase, projection, selection, selectionArgs, null, null, orderBy);

        return cursor;
    }



    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = mDatabase.insert(QuakeTable.NAME, null, values);

        if(id>-1){
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);

            getContext().getContentResolver().notifyChange(uri, null);

            return newUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(mUriMatcher.match(uri)){
            case SINGLE_ROW:
                String id = uri.getPathSegments().get(1);
                selection = QuakeTable.COL.KEY_ID + "=" + id
                    + (!TextUtils.isEmpty(selection)?
                    "AND (" + selection + ")" : "");
                break;
            case ALL_ROW:
                if(TextUtils.isEmpty(selection)) selection= "1";
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        int num = mDatabase.delete(QuakeTable.NAME, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return num;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch(mUriMatcher.match(uri)){
            case SINGLE_ROW:
                String id = uri.getPathSegments().get(1);
                selection = QuakeTable.COL.KEY_ID + "=" + id
                        + (!TextUtils.isEmpty(selection)?
                        "AND (" + selection + ")" : "");
                break;
            case ALL_ROW:
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        int num = mDatabase.update(QuakeTable.NAME, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return num;
    }
}
