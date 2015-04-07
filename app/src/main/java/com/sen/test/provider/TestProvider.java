package com.sen.test.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.sen.test.R;

/**
 * Editor: sgc
 * Date: 2015/04/06
 */
public class TestProvider extends ContentProvider {

    private static final String TABLE_NAME = "test";
    private static final int VERSION = 1;

    private SQLiteOpenHelper sqLiteOpenHelper;

    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {

        sqLiteOpenHelper = new TestSQLDatabase(getContext(), TABLE_NAME, null, VERSION);
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = getContext().getString(R.string.provider_authority);
        uriMatcher.addURI(authority, TestTable.TABLE_NAME, 1);
        uriMatcher.addURI(authority, TestTable.TABLE_NAME+"/#", 2);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        String tableName = getTable(uri);
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        String tableName = getTable(uri);
        long id = db.insert(tableName, null, values);
        if (id != -1) {
            uri = ContentUris.withAppendedId(uri, id);
            return uri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private String getTable(Uri uri) {

        switch (uriMatcher.match(uri)) {

            case 1:
            case 2:
                return TestTable.TABLE_NAME;
            default:

        }

        return null;
    }

    private class TestSQLDatabase extends SQLiteOpenHelper {

        public TestSQLDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public TestSQLDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TestTable.CREATE_TABLE_COMMAND);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(TestTable.CREATE_TABLE_COMMAND);
        }
    }
}
