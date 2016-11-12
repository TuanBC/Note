package cmc.note.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 13-Oct-16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "simple_note_app.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper mInstance = null;


    private static final String CREATE_TABLE_NOTE = "create table "
            + Constants.NOTES_TABLE
            + "("
            + Constants.COL_ID + " integer primary key autoincrement, "
            + Constants.COL_TITLE + " text not null, "
            + Constants.COL_CONTENT + " text, "
            + Constants.COL_TYPE + " text not null, "
            + Constants.COL_CREATED_TIME + " integer not null, "
            + Constants.COL_MODIFIED_TIME + " integer not null "
            + ")";

    private static final String CREATE_TABLE_CHECKLIST = "create table "
            + Constants.CL_TABLE
            + "("
            + Constants.COL_ID + " integer primary key autoincrement, "
            + Constants.COL_NOTEID + " integer not null, "
            + Constants.COL_TITLE + " text, "
            + Constants.COL_STATUS + " integer not null "
            + ")";

    private static final String CREATE_TABLE_CATEGORY = "create table "
            + Constants.CATEGORIES_TABLE
            + "("
            + Constants.COL_ID + " integer primary key autoincrement, "
            + Constants.COL_TITLE + " text not null, "
            + Constants.COL_CREATED_TIME + " integer not null, "
            + Constants.COL_MODIFIED_TIME + " integer not null "
            + ")";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_CHECKLIST);
        db.execSQL(CREATE_TABLE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.NOTES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.CL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.CATEGORIES_TABLE);

        onCreate(db);
    }
}