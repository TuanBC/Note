package cmc.note.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 21-Oct-16.
 */

public class ChecklistContentProvider extends ContentProvider{
    private DatabaseHelper dbHelper;                                       //Connect with the database

    private static final String BASE_PATH_CHECKLIST = "checklist";
    private static final String AUTHORITY = "cmc.note.data.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_CHECKLIST);
    private static final int ITEM = 100;
    private static final int ITEMS = 101;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_CHECKLIST, ITEMS);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_CHECKLIST + "/#", ITEM);
    }

    private void checkColumns(String[] projection) {
        if (projection != null) {
            HashSet<String> request = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> available = new HashSet<String>(Arrays.asList(Constants.CL_COLUMNS));
            if (!available.containsAll(request)) {
                throw new IllegalArgumentException("Unknown columns in projection - checklist");
            }
        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);

        int type = URI_MATCHER.match(uri);
        switch (type){
            case ITEMS:
                queryBuilder.setTables(Constants.CL_TABLE);
                break;
            case ITEM:
                queryBuilder.setTables(Constants.CL_TABLE);
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
            case ITEMS:
                id = db.insert(Constants.CL_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI at CL: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH_CHECKLIST + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int type = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows;
        switch (type) {
            case ITEMS:
                affectedRows = db.update(Constants.CL_TABLE, values, selection, selectionArgs);
                break;

            case ITEM:
                String id = uri.getLastPathSegment();
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

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int type = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows;
        switch (type) {
            case ITEMS:
                affectedRows = db.delete(Constants.CL_TABLE, selection, selectionArgs);
                break;

            case ITEM:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.delete(Constants.CL_TABLE, Constants.CL_COL_ID + "=" + id, null);
                } else {
                    affectedRows = db.delete(Constants.CL_TABLE, Constants.CL_COL_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRows;
    }



}
