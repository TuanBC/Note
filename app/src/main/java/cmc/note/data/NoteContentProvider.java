package cmc.note.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 12-Oct-16.
 */
public class NoteContentProvider extends android.content.ContentProvider {
    private DatabaseHelper dbHelper;                                       //Connect with the database

    private static final String BASE_PATH_NOTE = "notes";
    private static final String BASE_PATH_CHECK_ITEM = "check_items";
    private static final String BASE_PATH_CATEGORY = "categories";
    private static final String BASE_PATH_ATTACHMENT = "attachments";

    private static final String AUTHORITY = "cmc.note.data.provider";
    public static final Uri NOTE_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_NOTE);
    public static final Uri CL_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_CHECK_ITEM);
    public static final Uri CATEGORY_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_CATEGORY);
    public static final Uri ATTACH_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_ATTACHMENT);

    private static final int NOTE = 100;
    private static final int NOTES = 101;
    private static final int ITEM = 200;
    private static final int ITEMS = 201;
    private static final int CATEGORY = 300;
    private static final int CATEGORIES = 301;
    private static final int ATTACHMENT = 400;
    private static final int ATTACHMENTS = 401;

    private static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_NOTE, NOTES);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_NOTE + "/#", NOTE);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_CHECK_ITEM, ITEMS);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_CHECK_ITEM + "/#", ITEM);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_CATEGORY, CATEGORIES);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_CATEGORY + "/#", CATEGORY);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_ATTACHMENT, ATTACHMENTS);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH_ATTACHMENT + "/#", ATTACHMENT);
    }

    @Override
    public boolean onCreate() {
        dbHelper = DatabaseHelper.getInstance(getContext());
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
            case CATEGORIES:
                queryBuilder.setTables(Constants.CATEGORIES_TABLE);
                Log.i("LOG","case_CATEGORIES");
                break;
            case CATEGORY:
                queryBuilder.setTables(Constants.CATEGORIES_TABLE);
                Log.i("LOG","case_CATEGORY");
                break;
            case ATTACHMENTS:
                queryBuilder.setTables(Constants.ATTACHMENTS_TABLE);
                Log.i("LOG","case_ATTACHMENTS");
                break;
            case ATTACHMENT:
                queryBuilder.setTables(Constants.ATTACHMENTS_TABLE);
                Log.i("LOG","case_ATTACHMENT");
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
        Uri uri_temp;
        switch (type){
            case NOTES:
                id = db.insert(Constants.NOTES_TABLE, null, values);
                uri_temp = Uri.parse(BASE_PATH_NOTE + "/" + id);
                break;
            case ITEMS:
                id = db.insert(Constants.CL_TABLE, null, values);
                uri_temp = Uri.parse(BASE_PATH_CHECK_ITEM + "/" + id);
                break;
            case CATEGORIES:
                id = db.insert(Constants.CATEGORIES_TABLE, null, values);
                uri_temp = Uri.parse(BASE_PATH_CATEGORY + "/" + id);
                break;
            case ATTACHMENTS:
                id = db.insert(Constants.ATTACHMENTS_TABLE, null, values);
                uri_temp = Uri.parse(BASE_PATH_ATTACHMENT + "/" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI at notecontent: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri_temp;
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
                    affectedRows = db.delete(Constants.NOTES_TABLE, Constants.COL_ID + " = " + id, null);
                } else {
                    affectedRows = db.delete(Constants.NOTES_TABLE, Constants.COL_ID + " = " + id + " and " + selection, selectionArgs);
                }
                break;

            case ITEMS:
                affectedRows = db.delete(Constants.CL_TABLE, selection, selectionArgs);
                break;
            case ITEM:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.delete(Constants.CL_TABLE, Constants.COL_ID + " = " + id, null);
                } else {
                    affectedRows = db.delete(Constants.CL_TABLE, Constants.COL_ID + " = " + id + " and " + selection, selectionArgs);
                }
                break;

            case CATEGORIES:
                affectedRows = db.delete(Constants.CATEGORIES_TABLE, selection, selectionArgs);
                break;
            case CATEGORY:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.delete(Constants.CATEGORIES_TABLE, Constants.COL_ID + " = " + id, null);
                } else {
                    affectedRows = db.delete(Constants.CATEGORIES_TABLE, Constants.COL_ID + " = " + id + " and " + selection, selectionArgs);
                }
                break;

            case ATTACHMENTS:
                affectedRows = db.delete(Constants.ATTACHMENTS_TABLE, selection, selectionArgs);
                break;
            case ATTACHMENT:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.delete(Constants.ATTACHMENTS_TABLE, Constants.COL_ID + " = " + id, null);
                } else {
                    affectedRows = db.delete(Constants.ATTACHMENTS_TABLE, Constants.COL_ID + " = " + id + " and " + selection, selectionArgs);
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
                    affectedRows = db.update(Constants.NOTES_TABLE, values, Constants.COL_ID + "=" + id, null);
                } else {
                    affectedRows = db.update(Constants.NOTES_TABLE, values, Constants.COL_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case ITEMS:
                affectedRows = db.update(Constants.CL_TABLE, values, selection, selectionArgs);
                break;
            case ITEM:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.update(Constants.CL_TABLE, values, Constants.COL_ID + "=" + id, null);
                } else {
                    affectedRows = db.update(Constants.CL_TABLE, values, Constants.COL_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case CATEGORIES:
                affectedRows = db.update(Constants.CATEGORIES_TABLE, values, selection, selectionArgs);
                break;
            case CATEGORY:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.update(Constants.CATEGORIES_TABLE, values, Constants.COL_ID + "=" + id, null);
                } else {
                    affectedRows = db.update(Constants.CATEGORIES_TABLE, values, Constants.COL_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case ATTACHMENTS:
                affectedRows = db.update(Constants.ATTACHMENTS_TABLE, values, selection, selectionArgs);
                break;
            case ATTACHMENT:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    affectedRows = db.update(Constants.ATTACHMENTS_TABLE, values, Constants.COL_ID + "=" + id, null);
                } else {
                    affectedRows = db.update(Constants.ATTACHMENTS_TABLE, values, Constants.COL_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRows;
    }
}
