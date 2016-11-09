package cmc.note.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import cmc.note.models.Category;
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
    public Category getCategoryItem(Long id) {
        Category category;
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.CATEGORY_URI,
                Constants.CATEGORY_COLUMNS, Constants.COL_ID + " = " + id, null, null);

        Log.i("Log Cursor", "at " + id);

        if (cursor != null){
            cursor.moveToFirst();
            category = Category.getCategoryfromCursor(cursor);
            cursor.close();
            return category;
        }
        return null;
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
    }
}
