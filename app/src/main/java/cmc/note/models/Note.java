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
    private long dateCreated;
    private long dateModified;
    private String type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static Note getNotefromCursor(Cursor cursor){
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndex(Constants.COL_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(Constants.COL_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndex(Constants.COL_CONTENT)));
        note.setType(cursor.getString(cursor.getColumnIndex(Constants.COL_TYPE)));
        note.setDateCreated(cursor.getLong(cursor.getColumnIndex(Constants.COL_CREATED_TIME)));
        note.setDateModified(cursor.getLong(cursor.getColumnIndex(Constants.COL_MODIFIED_TIME)));

        return note;
    }
}
