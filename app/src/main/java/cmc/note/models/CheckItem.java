package cmc.note.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 19-Oct-16.
 */

public class CheckItem {
    private Long id;
    private Long noteId;
    private String name;
    private Boolean status = Boolean.FALSE;

//    public void reset() {
//        noteId = (long) 0;
//        name = null;
//        status = Boolean.FALSE;
//    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
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

    ////////////////////
    public static CheckItem getCheckItemfromCursor(Cursor cursor){
        CheckItem checkItem = new CheckItem();
        checkItem.setId(cursor.getLong(cursor.getColumnIndex(Constants.CL_COL_ID)));
        checkItem.setNoteId(cursor.getLong(cursor.getColumnIndex(Constants.CL_COL_NOTEID)));
        checkItem.setName(cursor.getString(cursor.getColumnIndex(Constants.CL_COL_NAME)));
        checkItem.setStatus(cursor.getInt(cursor.getColumnIndex(Constants.CL_COL_STATUS)) == 1);

        return checkItem;
    }
}
