package cmc.note.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cmc.note.models.Note;
import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 12-Oct-16.
 */

public class NoteManager {
    private Context mContext;
    private static NoteManager sNoteManagerInstance = null;

    public static NoteManager newInstance(Context context){

        if (sNoteManagerInstance == null){
            sNoteManagerInstance = new NoteManager(context.getApplicationContext());
        }

        return sNoteManagerInstance;
    }

    private NoteManager(Context context){
        this.mContext = context.getApplicationContext();
    }

    //(C)RUD
    public long create(Note note) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_TITLE, note.getTitle());
        values.put(Constants.COLUMN_CONTENT, note.getContent());
        values.put(Constants.COLUMN_MODIFIED_TIME, System.currentTimeMillis());
        Uri result = mContext.getContentResolver().insert(NoteContentProvider.CONTENT_URI, values);
        long id = Long.parseLong(result.getLastPathSegment());
        return id;
    }

    //C(R)UD
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.CONTENT_URI, Constants.COLUMNS, null , null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    notes.add(Note.getNotefromCursor(cursor));

                    // do what ever you want here
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return notes;
    }

    public Note getNote(Long id) {
        Note note;
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.CONTENT_URI,
                Constants.COLUMNS, Constants.COLUMN_ID + " = " + id, null, null);

        Log.i("Log Cursor", "at " + id);

        if (cursor != null){
            cursor.moveToFirst();
            note = Note.getNotefromCursor(cursor);
            return note;
        }
        return null;
    }

    //CR(U)D
    public void update(Note note) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_TITLE, note.getTitle());
        values.put(Constants.COLUMN_CONTENT, note.getContent());
        values.put(Constants.COLUMN_MODIFIED_TIME, System.currentTimeMillis());
        mContext.getContentResolver().update(NoteContentProvider.CONTENT_URI,
                values, Constants.COLUMN_ID  + "=" + note.getId(), null);

    }

    //CRU(D)
    public void delete(Note note) {
        mContext.getContentResolver().delete(
                NoteContentProvider.CONTENT_URI, Constants.COLUMN_ID + "=" + note.getId(), null);
    }


}
