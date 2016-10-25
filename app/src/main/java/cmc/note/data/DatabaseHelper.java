package cmc.note.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cmc.note.models.CheckItem;
import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 13-Oct-16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "simple_note_app.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_NOTE = "create table "
            + Constants.NOTES_TABLE
            + "("
            + Constants.NOTE_COL_ID + " integer primary key autoincrement, "
            + Constants.NOTE_COL_TITLE + " text not null, "
            + Constants.NOTE_COL_CONTENT + " text, "
            + Constants.NOTE_COL_TYPE + " text not null, "
            + Constants.NOTE_COL_CREATED_TIME + " integer not null, "
            + Constants.NOTE_COL_MODIFIED_TIME + " integer not null "
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CheckItem.getSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("some sql statement to do something");
    }
}