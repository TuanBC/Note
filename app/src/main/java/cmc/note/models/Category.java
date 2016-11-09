package cmc.note.models;

import android.database.Cursor;

import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 07-Nov-16.
 */

public class Category {
    private Long id;
    private String title;
    private long dateCreated;
    private long dateModified;


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

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public static Category getCategoryfromCursor(Cursor cursor){
        Category category = new Category();
        category.setId(cursor.getLong(cursor.getColumnIndex(Constants.COL_ID)));
        category.setTitle(cursor.getString(cursor.getColumnIndex(Constants.COL_TITLE)));
        return category;
    }
}
