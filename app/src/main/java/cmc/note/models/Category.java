package cmc.note.models;

import android.database.Cursor;

import cmc.note.activities.MainActivity;
import cmc.note.data.CategoryManager;
import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 07-Nov-16.
 */

public class Category {
    private Long id;
    private String title;
    private Long note_count;

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

    public Long getNoteCount() {
        return note_count;
    }

    public void setNoteCount(Long note_count) {
        this.note_count = note_count;
    }

    public static Category getCategoryfromCursor(Cursor cursor){
        Category category = new Category();
        category.setId(cursor.getLong(cursor.getColumnIndex(Constants.COL_ID)));
        category.setTitle(cursor.getString(cursor.getColumnIndex(Constants.COL_TITLE)));
        return category;
    }
}
