package cmc.note.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cmc.note.models.Category;
import cmc.note.models.Note;
import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 07-Nov-16.
 */

public class CategoryManager {
    private Context mContext;
    private static CategoryManager sCategoryManagerInstance = null;

    public static CategoryManager newInstance(Context context){

        if (sCategoryManagerInstance == null){
            sCategoryManagerInstance = new CategoryManager(context.getApplicationContext());
        }

        return sCategoryManagerInstance;
    }

    private CategoryManager (Context context){
        this.mContext = context.getApplicationContext();
    }

    //(C)RUD
    public long create(Category category) {
        ContentValues values = new ContentValues();
        values.put(Constants.COL_TITLE, category.getTitle());
        Uri result = mContext.getContentResolver().insert(NoteContentProvider.CATEGORY_URI, values); //BUG: insert command points to notecontentprovider's one
        long id = Long.parseLong(result.getLastPathSegment());
        return id;
    }

    //C(R)UD
    public List<Category> getAllCategoriesSortedBy(String input){
        List<Category> categories = new ArrayList<>();
        Cursor cursor = null;
        if (input==null) input="id_asc";
        switch (input){
            case ("id_asc"):
                cursor = mContext.getContentResolver().query(NoteContentProvider.CATEGORY_URI,
                        Constants.CATEGORY_COLUMNS, null, null, Constants.COL_ID + " ASC ");
                break;
            case ("abc_asc"):
                cursor = mContext.getContentResolver().query(NoteContentProvider.CATEGORY_URI,
                        Constants.CATEGORY_COLUMNS, null, null, Constants.COL_TITLE + " ASC ");
                break;
        }

        if (cursor != null){
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    categories.add(Category.getCategoryfromCursor(cursor));

                    // do what ever you want here
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        for (Category category: categories) {
            category.setNoteCount(getNoteCount(category.getId()));
        }

        return categories;
    }

    public Category getCategory(Long id) {
        Category category;
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.CATEGORY_URI,
                Constants.CATEGORY_COLUMNS, Constants.COL_ID + " = " + id, null, null);

        if (cursor != null){
            cursor.moveToFirst();
            category = Category.getCategoryfromCursor(cursor);
            cursor.close();
            return category;
        }
        return null;
    }

    public Category getFirstCategory(){
        Category category;
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.CATEGORY_URI,
                Constants.CATEGORY_COLUMNS, null, null, Constants.COL_ID + " ASC ");
        if (cursor != null){
            cursor.moveToFirst();
            category = Category.getCategoryfromCursor(cursor);
            cursor.close();
            return category;
        }
        return null;
    }

    private long getNoteCount(Long id) {
        long i=0;
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.NOTE_URI,
                Constants.NOTE_COLUMNS, Constants.COL_CATID + " = " + id, null, null);

        Log.i("Log Cursor", "at " + id);

        if (cursor != null){
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    i++;
                    // do what ever you want here
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return i;
    }

    //CR(U)D
    public void update(Category Category) {
        ContentValues values = new ContentValues();
        values.put(Constants.COL_TITLE, Category.getTitle());

        mContext.getContentResolver().update(NoteContentProvider.CATEGORY_URI,
                values, Constants.COL_ID + "=" + Category.getId(), null);
    }

    //CRU(D)
    public void delete(Long id){
        mContext.getContentResolver().delete(
                NoteContentProvider.CATEGORY_URI, Constants.COL_ID + "=" + id, null);
        mContext.getContentResolver().delete(
                NoteContentProvider.NOTE_URI, Constants.COL_CATID + "=" + id, null);
    }
}
