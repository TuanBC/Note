package cmc.note.ultilities;

/**
 * Created by tuanb on 10-Oct-16.
 */

public class Constants {
    public static final String NOTES_TABLE = "notes";

    public static final String NOTE_COL_ID = "_id";
    public static final String NOTE_COL_TITLE = "title";
    public static final String NOTE_COL_CONTENT = "content";
    public static final String NOTE_COL_CREATED_TIME = "created_time";
    public static final String NOTE_COL_MODIFIED_TIME = "modified_time";
    public static final String NOTE_COL_TYPE = "type";

    public static final String[] NOTE_COLUMNS = {
            Constants.NOTE_COL_ID,
            Constants.NOTE_COL_TITLE,
            Constants.NOTE_COL_CONTENT,
            Constants.NOTE_COL_TYPE,
            Constants.NOTE_COL_CREATED_TIME,
            Constants.NOTE_COL_MODIFIED_TIME
    };

    public static final String CL_TABLE = "check_items";

    public static final String CL_COL_ID = "_id";
    public static final String CL_COL_NOTEID = "note_id";
    public static final String CL_COL_NAME = "name";
    public static final String CL_COL_STATUS = "status";

    public static final String[] CL_COLUMNS = {
            Constants.CL_COL_ID,
            Constants.CL_COL_NOTEID,
            Constants.CL_COL_NAME,
            Constants.CL_COL_STATUS
    };
}
