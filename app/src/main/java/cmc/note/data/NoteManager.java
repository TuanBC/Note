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
        values.put(Constants.COL_TITLE, note.getTitle());
        values.put(Constants.COL_CONTENT, note.getContent());
        values.put(Constants.COL_CREATED_TIME, System.currentTimeMillis());
        values.put(Constants.COL_MODIFIED_TIME, System.currentTimeMillis());
        values.put(Constants.COL_TYPE, note.getType());
        values.put(Constants.COL_CATID, note.getCategoryId());
        Uri result = mContext.getContentResolver().insert(NoteContentProvider.NOTE_URI, values);
        long id = Long.parseLong(result.getLastPathSegment());
        Log.i("Log Cursor"," create note name  "+id + " "  );
        return id;
    }

    //C(R)UD
    public List<Note> getAllNotesWithKey(String text) {
        List<Note> temp_1 = this.getAllNotesWithKeyTitle(text);
        List<Note> temp_2 = this.getAllNotesWithKeyContent(text);
        for (Note note: temp_2){
            if (!temp_1.contains(note)){
                temp_1.add(note);
            } else break;
        }
        return temp_1;
    }

    private List<Note> getAllNotesWithKeyTitle(String text) {
        List<Note> notes = new ArrayList<>();
        if (text.equals("")) return notes;
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.NOTE_URI,
                Constants.NOTE_COLUMNS, Constants.COL_TITLE + " LIKE '%" + text +"%'", null, null);
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

    private List<Note> getAllNotesWithKeyContent(String text) {
        List<Note> notes = new ArrayList<>();
        if (text.equals("")) return notes;
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.NOTE_URI,
                Constants.NOTE_COLUMNS, Constants.COL_CONTENT + " LIKE '%" + text +"%'", null, null);
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

    public List<Note> getAllNotesSortedBy(String input){
        List<Note> notes = new ArrayList<>();
        Cursor cursor = null;
        if (input==null) input="id_asc";
        switch (input){
            case ("id_asc"):
                cursor = mContext.getContentResolver().query(NoteContentProvider.NOTE_URI,
                        Constants.NOTE_COLUMNS, null, null, Constants.COL_ID + " ASC ");
                break;
            case ("abc_asc"):
                cursor = mContext.getContentResolver().query(NoteContentProvider.NOTE_URI,
                        Constants.NOTE_COLUMNS, null, null, Constants.COL_TITLE + " ASC ");
                break;
            case ("created_desc"):
                cursor = mContext.getContentResolver().query(NoteContentProvider.NOTE_URI,
                        Constants.NOTE_COLUMNS, null, null, Constants.COL_CREATED_TIME + " DESC ");
                break;
            case ("modified_desc"):
                cursor = mContext.getContentResolver().query(NoteContentProvider.NOTE_URI,
                        Constants.NOTE_COLUMNS, null, null, Constants.COL_MODIFIED_TIME + " DESC ");
                break;
        }

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
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.NOTE_URI,
                Constants.NOTE_COLUMNS, Constants.COL_ID + " = " + id, null, null);

        Log.i("Log Cursor", "at " + id);

        if (cursor != null){
            cursor.moveToFirst();
            note = Note.getNotefromCursor(cursor);
            cursor.close();
            return note;
        }
        return null;
    }

    public Note getNoteByTitle(String title) {
        Note note;
        Cursor cursor = mContext.getApplicationContext().getContentResolver().query(NoteContentProvider.NOTE_URI,
                Constants.NOTE_COLUMNS, Constants.COL_TITLE + " = '" + title + "'", null, null);

        Log.i("Log Cursor", "at " + title);

        if (cursor != null){
            cursor.moveToFirst();
            note = Note.getNotefromCursor(cursor);
            cursor.close();
            return note;
        }
        return null;
    }

    public List<Note> getNotesByCategoryId(Long id) {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = mContext.getApplicationContext().getContentResolver().query(NoteContentProvider.NOTE_URI,
                Constants.NOTE_COLUMNS, Constants.COL_CATID + " = " + id, null, null);

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

    public Note getLastNote() {
        Note note;
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.NOTE_URI,
                Constants.NOTE_COLUMNS, null, null, Constants.COL_MODIFIED_TIME + " DESC ");
        if (cursor != null){
            cursor.moveToFirst();
            note = Note.getNotefromCursor(cursor);
            cursor.close();
            return note;
        }
        return null;
    }

    //CR(U)D
    public void update(Note note) {
        ContentValues values = new ContentValues();
        values.put(Constants.COL_TITLE, note.getTitle());
        values.put(Constants.COL_CONTENT, note.getContent());
        values.put(Constants.COL_CREATED_TIME, note.getDateCreated());
        values.put(Constants.COL_MODIFIED_TIME, System.currentTimeMillis());
        values.put(Constants.COL_CATID, note.getCategoryId());
        mContext.getApplicationContext().getContentResolver().update(NoteContentProvider.NOTE_URI,
                values, Constants.COL_ID + "=" + note.getId(), null);

    }

    //CRU(D)
    public void delete(Note note) {
        mContext.getContentResolver().delete(
                NoteContentProvider.NOTE_URI, Constants.COL_ID + "=" + note.getId(), null);
    }
}
