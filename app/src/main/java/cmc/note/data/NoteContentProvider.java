package cmc.note.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 12-Oct-16.
 */
public class NoteContentProvider extends android.content.ContentProvider {
    private DatabaseHelper dbHelper;                                       //Connect with the database

    private static final String BASE_PATH_NOTE = "notes";
    private static final String BASE_PATH_CHECK_ITEM = "check_items";
    private static final String AUTHORITY = "cmc.note.data.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_NOTE);
    public static final Uri CL_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_CHECK_ITEM);
    private static final int NOTE = 100;
    private static final int NOTES = 101;
    private static final int ITEM = 200;
    private static final int ITEMS = 201;

    private static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_NOTE, NOTES);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_NOTE + "/#", NOTE);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_CHECK_ITEM, ITEMS);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_CHECK_ITEM + "/#", ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//        checkColumns(projection);

        int type = URI_MATCHER.match(uri);
        switch (type){
            case NOTES:
                queryBuilder.setTables(Constants.NOTES_TABLE);
                Log.i("LOG","case_NOTES");
                break;
            case NOTE:
                queryBuilder.setTables(Constants.NOTES_TABLE);
                Log.i("LOG","case_NOTE");
                break;
            case ITEMS:
                queryBuilder.setTables(Constants.CL_TABLE);
                Log.i("LOG","case_ITEMS");
                break;
            case ITEM:
                queryBuilder.setTables(Constants.CL_TABLE);
                Log.i("LOG","case_ITEM");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int type = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Long id;
        switch (type){
            case NOTES:
                id = db.insert(Constants.NOTES_TABLE, null, values);
                break;
            case ITEMS:
                id = db.insert(Constants.CL_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI at notecontent: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH_NOTE + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int type = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows;
        String id;
        switch (type) {
            case NOTES:
                affectedRows = db.delete(Constants.NOTES_TABLE, selection, selectionArgs);
                break;

            case NOTE:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.delete(Constants.NOTES_TABLE, Constants.NOTE_COL_ID + " = " + id, null);
                } else {
                    affectedRows = db.delete(Constants.NOTES_TABLE, Constants.NOTE_COL_ID + " = " + id + " and " + selection, selectionArgs);
                }
                break;

            case ITEMS:
                affectedRows = db.delete(Constants.CL_TABLE, selection, selectionArgs);
                break;

            case ITEM:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.delete(Constants.CL_TABLE, Constants.CL_COL_ID + " = " + id, null);
                } else {
                    affectedRows = db.delete(Constants.CL_TABLE, Constants.CL_COL_ID + " = " + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int type = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows;
        String id;
        switch (type) {
            case NOTES:
                affectedRows = db.update(Constants.NOTES_TABLE, values, selection, selectionArgs);
                break;

            case NOTE:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.update(Constants.NOTES_TABLE, values, Constants.NOTE_COL_ID + "=" + id, null);
                } else {
                    affectedRows = db.update(Constants.NOTES_TABLE, values, Constants.NOTE_COL_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case ITEMS:
                affectedRows = db.update(Constants.CL_TABLE, values, selection, selectionArgs);
                break;

            case ITEM:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.update(Constants.CL_TABLE, values, Constants.CL_COL_ID + "=" + id, null);
                } else {
                    affectedRows = db.update(Constants.CL_TABLE, values, Constants.CL_COL_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRows;
    }
}
