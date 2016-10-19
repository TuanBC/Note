package cmc.note.models;

import android.database.Cursor;

import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 10-Oct-16.
 */

public class Note {
    private Long id;
    private String title;
    private String content;
    private long dateModified;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static Note getNotefromCursor(Cursor cursor){
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_CONTENT)));
        note.setDateModified(cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_MODIFIED_TIME)));

        return note;
    }
}
