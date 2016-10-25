package cmc.note.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cmc.note.models.CheckItem;
import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 21-Oct-16.
 */

public class ChecklistManager {
    private Context clContext;
    private static ChecklistManager sChecklistManagerInstance = null;

    public static ChecklistManager newInstance(Context context){

        if (sChecklistManagerInstance == null){
            sChecklistManagerInstance = new ChecklistManager(context.getApplicationContext());
        }

        return sChecklistManagerInstance;
    }

    private ChecklistManager (Context context){
        this.clContext = context.getApplicationContext();
    }

    //(C)RUD
    public long create(CheckItem item) {
        ContentValues values = new ContentValues();
        values.put(Constants.CL_COL_ID, item.getId());
        values.put(Constants.CL_COL_NAME, item.getName());
        values.put(Constants.CL_COL_NOTEID, 0);
        values.put(Constants.CL_COL_STATUS, item.getStatus());
        Uri result = clContext.getContentResolver().insert(NoteContentProvider.CONTENT_URI, values); //BUG: insert command points to notecontentprovider's one
        long id = Long.parseLong(result.getLastPathSegment());
        return id;
    }

    //C(R)UD
    public CheckItem getChecklistItem(Long id) {
        CheckItem item;
        Cursor cursor = clContext.getContentResolver().query(NoteContentProvider.CONTENT_URI,
                Constants.CL_COLUMNS, Constants.CL_COL_ID + " = " + id, null, null);

        Log.i("Log Cursor", "at " + id);

        if (cursor != null){
            cursor.moveToFirst();
            item = CheckItem.getCheckItemfromCursor(cursor);
            return item;
        }
        return null;
    }


    public List<CheckItem> getChecklistItemByNoteId(Long id) {
        List<CheckItem> checkItems = new ArrayList<>();

        Cursor cursor = clContext.getContentResolver().query(NoteContentProvider.CONTENT_URI,
                Constants.CL_COLUMNS, Constants.CL_COL_NOTEID + " = " + id, null, null);

        Log.i("Log Cursor", "note_at " + id);

        if (cursor != null){
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    checkItems.add(CheckItem.getCheckItemfromCursor(cursor));
                    // do what ever you want here
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return checkItems;
    }

    //CR(U)D
    public void update(CheckItem checkItem) {
        ContentValues values = new ContentValues();
        values.put(Constants.CL_COL_NAME, checkItem.getName());
        values.put(Constants.CL_COL_STATUS, checkItem.getStatus());

        clContext.getContentResolver().update(NoteContentProvider.CONTENT_URI,
                values, Constants.CL_COL_ID + "=" + checkItem.getId(), null);
    }

    //CRU(D)
    public void delete(CheckItem checkItem) {
        clContext.getContentResolver().delete(
                NoteContentProvider.CONTENT_URI, Constants.CL_COL_ID + "=" + checkItem.getId(), null);
    }

    public void deleteByNoteId(Long id){
        clContext.getContentResolver().delete(
                NoteContentProvider.CONTENT_URI, Constants.CL_COL_NOTEID + "=" + id, null);
    }


}
