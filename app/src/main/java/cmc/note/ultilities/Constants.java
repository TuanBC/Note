package cmc.note.ultilities;

/**
 * Created by tuanb on 10-Oct-16.
 */

public class Constants {
    public static final String NOTES_TABLE = "notes";

    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT = "content";
    public static final String COL_CREATED_TIME = "created_time";
    public static final String COL_MODIFIED_TIME = "modified_time";
    public static final String COL_TYPE = "type";
    public static final String COL_CATID = "category_id";

    public static final String[] NOTE_COLUMNS = {
            Constants.COL_ID,
            Constants.COL_TITLE,
            Constants.COL_CONTENT,
            Constants.COL_TYPE,
            Constants.COL_CATID,
            Constants.COL_CREATED_TIME,
            Constants.COL_MODIFIED_TIME
    };

    public static final String CL_TABLE = "check_items";

    public static final String COL_NOTEID = "note_id";
    public static final String COL_STATUS = "status";

    public static final String[] CL_COLUMNS = {
            Constants.COL_ID,
            Constants.COL_NOTEID,
            Constants.COL_TITLE,
            Constants.COL_STATUS
    };

    public static final String CATEGORIES_TABLE = "categories";

    public static final String[] CATEGORY_COLUMNS = {
            Constants.COL_ID,
            Constants.COL_TITLE
    };
}
