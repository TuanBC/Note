package cmc.note.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cmc.note.models.Attachment;
import cmc.note.ultilities.Constants;

/**
 * Created by tuanb on 24-Nov-16.
 */

public class AttachmentManager {
    private Context mContext;
    private static AttachmentManager sAttachmentManagerInstance = null;

    public static AttachmentManager newInstance(Context context){

        if (sAttachmentManagerInstance == null){
            sAttachmentManagerInstance = new AttachmentManager(context.getApplicationContext());
        }

        return sAttachmentManagerInstance;
    }

    private AttachmentManager (Context context){
        this.mContext = context.getApplicationContext();
    }

    //(C)RUD
    public long create(Attachment attachment) {
        ContentValues values = new ContentValues();
        values.put(Constants.COL_PATH, attachment.getPath());
        values.put(Constants.COL_NOTEID, attachment.getNoteId());
        Uri result = mContext.getContentResolver().insert(NoteContentProvider.ATTACH_URI, values); //BUG: insert command points to notecontentprovider's one
        long id = Long.parseLong(result.getLastPathSegment());
        return id;
    }

    //C(R)UD

//    public List<Attachment> getAllAttachmentsSortedBy(String input){
//        List<Attachment> Attachments = new ArrayList<>();
//        Cursor cursor = null;
//        if (input==null) input="id_asc";
//        switch (input){
//            case ("id_asc"):
//                cursor = mContext.getContentResolver().query(NoteContentProvider.ATTACH_URI,
//                        Constants.ATTACHMENT_COLUMNS, null, null, Constants.COL_ID + " ASC ");
//                break;
//            case ("abc_asc"):
//                cursor = mContext.getContentResolver().query(NoteContentProvider.ATTACH_URI,
//                        Constants.ATTACHMENT_COLUMNS, null, null, Constants.COL_TITLE + " ASC ");
//                break;
//        }
//
//        if (cursor != null){
//            if (cursor.moveToFirst()){
//                while(!cursor.isAfterLast()){
//                    Attachments.add(Attachment.getAttachmentfromCursor(cursor));
//
//                    // do what ever you want here
//                    cursor.moveToNext();
//                }
//            }
//            cursor.close();
//        }
//
//        return Attachments;
//    }

    public Attachment getAttachmentByNoteId(Long id) {
        Attachment attachment;
        Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.ATTACH_URI,
                Constants.ATTACHMENT_COLUMNS, Constants.COL_NOTEID + " = " + id, null, null);

        if (cursor != null){
            cursor.moveToFirst();
            attachment = Attachment.getAttachmentfromCursor(cursor);
            cursor.close();
            return attachment;
        }
        return null;
    }


    //CR(U)D
    public void update(Attachment attachment) {
        ContentValues values = new ContentValues();
        values.put(Constants.COL_PATH, attachment.getPath());

        mContext.getContentResolver().update(NoteContentProvider.ATTACH_URI,
                values, Constants.COL_ID + "=" + attachment.getId(), null);
    }

    //CRU(D)
    public void delete(Long id){
        mContext.getContentResolver().delete(
                NoteContentProvider.ATTACH_URI, Constants.COL_ID + "=" + id, null);
    }

    public void deleteByNoteId(Long id){
        mContext.getContentResolver().delete(
                NoteContentProvider.ATTACH_URI, Constants.COL_NOTEID + "=" + id, null);
    }
}
