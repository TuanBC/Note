package cmc.note.models;

import android.database.Cursor;

import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 24-Nov-16.
 */

public class Attachment {
    private Long id;
    private Long noteId;
    private String path;
    private Boolean is_image = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean isImage() {
        return is_image;
    }

    public void setIsImage(Boolean is_image) {
        this.is_image = is_image;
    }

    public static Attachment getAttachmentfromCursor(Cursor cursor){
        Attachment attachment = new Attachment();
        attachment.setId(cursor.getLong(cursor.getColumnIndex(Constants.COL_ID)));
        attachment.setPath(cursor.getString(cursor.getColumnIndex(Constants.COL_PATH)));
        attachment.setNoteId(cursor.getLong(cursor.getColumnIndex(Constants.COL_NOTEID)));
        return attachment;
    }
}
