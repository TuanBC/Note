package cmc.note.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Created by tuanb on 19-Oct-16.
 */

public class CheckItem {
    public static final String TABLE_NAME = "check_items";

    public static final String COL_ID = "_id";
    public static final String COL_NOTEID = "note_id";
    public static final String COL_NAME = "name";
    public static final String COL_STATUS = "status";

    public static String getSql() {
        return concat("CREATE TABLE ", TABLE_NAME, " (",
                COL_ID, " INTEGER PRIMARY KEY AUTOINCREMENT, ",
                COL_NOTEID, " INTEGER, ",
                COL_NAME, " TEXT not null, ",
                COL_STATUS, " INTEGER not null ",
                ");");
    }

    long save(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTEID, noteId);
        cv.put(COL_NAME, name==null ? "" : name);
        cv.put(COL_STATUS, status ? 1 : 0);

        return db.insert(TABLE_NAME, null, cv);
    }

    boolean update(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, id);
        if (noteId > 0)
            cv.put(COL_NOTEID, noteId);
        if (name != null)
            cv.put(COL_NAME, name);
        if (status != null)
            cv.put(COL_STATUS, status ? 1 : 0);

        return db.update(TABLE_NAME, cv, COL_ID + " = ?", new String[]{String.valueOf(id)})
                == 1;
    }

    public boolean load(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, null, COL_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                reset();
                id = cursor.getLong(cursor.getColumnIndex(COL_ID));
                noteId = cursor.getLong(cursor.getColumnIndex(COL_NOTEID));
                name = cursor.getString(cursor.getColumnIndex(COL_NAME));
                status = cursor.getInt(cursor.getColumnIndex(COL_STATUS)) == 1;
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }
    }

    public static Cursor list(SQLiteDatabase db, String noteId) {
        if (noteId != null)
            return db.query(TABLE_NAME, null, COL_NOTEID+" = ?", new String[]{noteId}, null, null, COL_ID+" ASC");
        else
            return null;
    }

    public boolean delete(SQLiteDatabase db) {
        return db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)})
                == 1;
    }

    //--------------------------------------------------------------------------

    private long id;
    private long noteId;
    private String name;
    private Boolean status = Boolean.FALSE;

    public void reset() {
        id = 0;
        noteId = 0;
        name = null;
        status = Boolean.FALSE;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getNoteId() {
        return noteId;
    }
    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    public void check() {
        this.status =! this.status;
    }

    public CheckItem() {}

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!((obj == null) || (obj.getClass() != this.getClass())))
            if (id == ((CheckItem) obj).id)
                return true;
        return false;

    }

    @Override
    public int hashCode() {
        return 1;
    }

    public static String concat(Object... objects) {
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        for (Object obj : objects) {
            sb.append(obj);
        }
        return sb.toString();
    }

    ////////////////////
    public static CheckItem getCheckItemfromCursor(Cursor cursor){
        CheckItem checkItem = new CheckItem();
        checkItem.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
        checkItem.setNoteId(cursor.getLong(cursor.getColumnIndex(COL_NOTEID)));
        checkItem.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
        checkItem.setStatus(cursor.getInt(cursor.getColumnIndex(COL_STATUS)) == 1);

        return checkItem;
    }
}
