package cmc.note.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cmc.note.R;
import cmc.note.activities.MainActivity;
import cmc.note.adapter.AttachListAdapter;
import cmc.note.data.AttachmentManager;
import cmc.note.data.CategoryManager;
import cmc.note.data.NoteManager;
import cmc.note.models.Attachment;
import cmc.note.models.Category;
import cmc.note.models.Note;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tuanb on 10-Oct-16.
 */

public class NoteLinedEditorFragment extends Fragment {
    private View mRootView;
    private EditText mTitleEditText;
    private EditText mContentEditText;
    private Note mCurrentNote;
    private TextView mTimeAgo;
    private TextView mModifiedTime;

    private String mListOrder;
    private String mImageAttachment;
    private String tempImagePath;
    private List<String> mFileAttachment;

    private boolean buttons_clicked = false;

    private Calendar mReminderDate = null;

    private long mCategoryId;

    private static final int CHOOSE_IMAGE_REQ_CODE = 1;
    private static final int CHOOSE_FILE_REG_CODE = 2;
    private static final int TAKE_IMAGE_REQ_CODE = 3;

    private final static String TAG = "NOTEEDITOR Fragment";

    public NoteLinedEditorFragment() {
        // Required empty public constructor
    }

    public static NoteLinedEditorFragment newInstance(long id) {
        NoteLinedEditorFragment fragment = new NoteLinedEditorFragment();

        if (id > 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("id", id);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    public void setListOrder(String s) {
        this.mListOrder = s;
    }

    //onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getCurrentNote();
    }

    private void getCurrentNote() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            Long id = args.getLong("id", 0);
            Log.i("log getcurrentnote", "id" + id);
            if (id > 0) {
                mCurrentNote = NoteManager.newInstance(getActivity()).getNote(id);
                mCategoryId = mCurrentNote.getCategoryId();
                mImageAttachment = AttachmentManager.newInstance(getActivity()).getAttachmentByNoteId(mCurrentNote.getId()).getPath();
            }
        }
    }
    //
    //onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note_lined_editor, container, false);
        mTitleEditText = (EditText) mRootView.findViewById(R.id.edit_text_title);
        mContentEditText = (EditText) mRootView.findViewById(R.id.edit_text_note);
        mTimeAgo = (TextView) mRootView.findViewById(R.id.time_ago);
        mModifiedTime = (TextView) mRootView.findViewById(R.id.modified_time);

        if (mCategoryId==0){
            mCategoryId = CategoryManager.newInstance(getActivity()).getFirstCategory().getId();
        }

        mRootView.setFocusableInTouchMode(true);
        mRootView.requestFocus();
//        mRootView.setOnKeyListener( new View.OnKeyListener()
//        {
//            @Override
//            public boolean onKey( View v, int keyCode, KeyEvent event )
//            {
//                if( keyCode != KeyEvent.FLAG_SOFT_KEYBOARD )
//                {
//                    getActivity().finish();
//                    return true;
//                }
//                return false;
//            }
//        } );

        mRootView.findViewById(R.id.btn_category).setOnClickListener(button_listener);
        mRootView.findViewById(R.id.btn_reminder).setOnClickListener(button_listener);
        mRootView.findViewById(R.id.btn_attach).setOnClickListener(button_listener);

        RecyclerView mRecylerView = (RecyclerView) mRootView.findViewById(R.id.attach_list_recycler_view);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecylerView.setAdapter(new AttachListAdapter(mFileAttachment, getActivity()));
        mRecylerView.setItemAnimator(new DefaultItemAnimator());

        if (mImageAttachment==null){
            mRootView.findViewById(R.id.iv_attach).setVisibility(View.GONE);
        }

        return mRootView;
    }

    private boolean saveNote() {
        String content = mContentEditText.getText().toString();
        String title = mTitleEditText.getText().toString();
        if (TextUtils.isEmpty(title)) {
            title = "Untitled Note";
        }

        if (mReminderDate != null) {
            MainActivity a = (MainActivity) getActivity();
            a.scheduleNotification(a.getNotification(title), mReminderDate.getTimeInMillis() - System.currentTimeMillis());
        }

        if (mCurrentNote != null) {
            mCurrentNote.setContent(content);
            mCurrentNote.setTitle(title);
            mCurrentNote.setCategoryId(mCategoryId);
            NoteManager.newInstance(getActivity()).update(mCurrentNote);

            if (mImageAttachment != null){
                Attachment attachment = AttachmentManager.newInstance(getActivity()).getAttachmentByNoteId(mCurrentNote.getId());
                if (attachment==null) {
                    attachment = new Attachment();
                    attachment.setNoteId(mCurrentNote.getId());
                    attachment.setPath(mImageAttachment);
                    attachment.setIsImage(true);
                    AttachmentManager.newInstance(getActivity()).create(attachment);
                } else {
                    attachment.setPath(mImageAttachment);
                    AttachmentManager.newInstance(getActivity()).update(attachment);
                }
            }

            //************************************
            //ADD CHECK ATTACHMENT TYPE TO DATABASE
            //***********************************

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("list_order", mListOrder);
            startActivity(intent);
        } else {
            Note note = new Note();
            note.setTitle(title);
            note.setType("note");
            note.setContent(content);
            note.setCategoryId(mCategoryId);
            NoteManager.newInstance(getActivity()).create(note);

            Attachment attachment = new Attachment();
            attachment.setNoteId(NoteManager.newInstance(getActivity()).getLastNote().getId());
            attachment.setPath(mImageAttachment);
            AttachmentManager.newInstance(getActivity()).create(attachment);

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("list_order", mListOrder);
            startActivity(intent);
        }

        return true;
    }

    //onResume
    @Override
    public void onResume() {
//        makeToast("RESUMED");
        super.onResume();
        if (mCurrentNote != null) {
            populateFields();
        } else mModifiedTime.setText(getReadableDate(System.currentTimeMillis()));

        if (mImageAttachment!=null){
            File imgFile = new File(mImageAttachment);
            if(imgFile.exists()){
                mRootView.findViewById(R.id.iv_attach).setVisibility(View.VISIBLE);
                Bitmap myBitmap = decodeFile(imgFile);
                ImageView myImage = (ImageView) mRootView.findViewById(R.id.iv_attach);
                myImage.setMaxHeight(myBitmap.getHeight());
                myImage.setImageBitmap(myBitmap);
            } else {
                mRootView.findViewById(R.id.iv_attach).setVisibility(View.GONE);
                makeToast("Image not found");
            }
        }

        Button mButton = (Button) mRootView.findViewById(R.id.btn_category);
        mButton.setText(CategoryManager.newInstance(getActivity()).getCategory(mCategoryId).getTitle());
    }

    // Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            Point size = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = size.y;

            // The new size we want to scale to
            final int REQUIRED_WIDTH = width/2;
            final int REQUIRED_HEIGHT = height/2;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HEIGHT)
            {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    private void populateFields() {
        mTitleEditText.setText(mCurrentNote.getTitle());
        mContentEditText.setText(mCurrentNote.getContent());

        //TIME
        long temp_modified_time = mCurrentNote.getDateModified();
        mModifiedTime.setText(getReadableDate(temp_modified_time));

        long temp_time_ago = (System.currentTimeMillis() - temp_modified_time) / 1000;
        if (temp_time_ago < 60) mTimeAgo.setText(temp_time_ago + " seconds ago");
        else if (temp_time_ago < 3600) mTimeAgo.setText(temp_time_ago / 60 + " minute(s) ago");
        else if (temp_time_ago < 86400) mTimeAgo.setText(temp_time_ago / 3600 + " hour(s) ago");
        else mTimeAgo.setText(temp_time_ago / 86400 + " day(s) ago");
    }

    private static String getReadableDate(long date) {
        return new SimpleDateFormat("MMM dd, yyyy").format(new Date(date));
    }
    //

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_note_edit, menu);
    }

    //MENU ONCLICK LISTENER
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (saveNote()) { //IF NULL => BLANK NOTE
                    makeToast(mCurrentNote != null ? "Note updated" : "Note saved");
                }
                break;

            case R.id.action_delete:
                if (mCurrentNote != null) {
                    promptToDelete();
                } else {
                    getActivity().finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void promptToDelete() {
        final String titleOfNoteTobeDeleted = mCurrentNote.getTitle();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Delete " + titleOfNoteTobeDeleted + " ?");
        alertDialog.setMessage("Are you sure you want to delete the note " + titleOfNoteTobeDeleted + "?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AttachmentManager.newInstance(getActivity()).deleteByNoteId(mCurrentNote.getId());

                NoteManager.newInstance(getActivity()).delete(mCurrentNote);
                makeToast(titleOfNoteTobeDeleted + "deleted");

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("list_order", mListOrder);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void onBackClicked() {
        promptToDiscard();
    }

    public void promptToDiscard() {
        Note temp_note = NoteManager.newInstance(getActivity()).getLastNote();
        if (temp_note.getTitle().equals(""))
            NoteManager.newInstance(getActivity()).delete(temp_note);

        if (mRootView.isFocused() && !buttons_clicked) {
            getActivity().finish();
        }
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Discard?");
            alertDialog.setMessage("Are you sure you want to go back? All changes will be discarded");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    //

    private View.OnClickListener button_listener = new View.OnClickListener() {
        public void onClick(View v) {
            buttons_clicked=true;
            switch (v.getId()) {
                case R.id.btn_reminder:
                    if (mReminderDate != null) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Reminder");
                        alertDialog.setMessage("What do you want to do?");
                        alertDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                createDateTimePicker();
                            }
                        });
                        alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mReminderDate = null;
                                ImageButton imageButton = (ImageButton) mRootView.findViewById(R.id.btn_reminder);
                                imageButton.setImageResource(R.drawable.ic_action_reminder);
                            }
                        });
                        alertDialog.show();
                    } else createDateTimePicker();

//                    TimePickerFragment newFragment = new TimePickerFragment();
//
//                    Bundle args = new Bundle();
//                    if (mCurrentNote.getId()>0)
//                        args.putString("note_title", mCurrentNote.getTitle());
//                    else
//                    newFragment.setArguments(args);
//                    newFragment.show(getActivity().getFragmentManager(),"TimePicker");
                    break;
                case R.id.btn_category:
                    promptToChangeCategory();
                    break;
                case R.id.btn_attach:
                    promptToAttach();
                    break;
            }
        }
    };

    private void promptToChangeCategory(){
        List<Category> categories= CategoryManager.newInstance(getActivity()).getAllCategoriesSortedBy(null);
        int i = 0;
        for (Category category: categories) {
            i++;
        }
        String[] array_categories = new String[i];
        i=0;
        for (Category category: categories) {
            array_categories[i] = category.getTitle();
            i++;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.choose_category)
                .setItems(array_categories, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCategoryId = which+1;
                        NoteLinedEditorFragment myParentFragment = (NoteLinedEditorFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                        myParentFragment.onResume();
                    }
                });
        alertDialog.show();
    }

    private void promptToAttach(){
        String[] array_sort = {"Choose photo", "Take photo", "Choose file"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.attach)
                .setItems(array_sort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                chooseImage();
                                break;
                            case 1:
                                takeImage();
                                break;
                            case 2:
                                chooseFile();
                                break;
                        }
                    }
                });
        alertDialog.show();
    }

    private void createDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        mReminderDate = date;

                        ImageButton imageButton = (ImageButton) mRootView.findViewById(R.id.btn_reminder);
                        imageButton.setImageResource(R.drawable.ic_action_reminder_checked);
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        },currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        dialog.show();
    }

    public void chooseImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, CHOOSE_IMAGE_REQ_CODE);
    }

    public void takeImage(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_"+ timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);

        tempImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(tempImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, TAKE_IMAGE_REQ_CODE);
    }

    private void chooseFile() {
        makeToast("Not completed, only image. Researching");

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("file/*");
        getIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("*/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select File");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, CHOOSE_FILE_REG_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_IMAGE_REQ_CODE) {
                Uri uri = data.getData();
                mImageAttachment = getRealPathFromURI(uri); //data.getdata = uri

                makeToast("Photo attachment added");
            } else if (requestCode == CHOOSE_FILE_REG_CODE) {
                ContentResolver cr = getActivity().getContentResolver();
                Uri uri = data.getData();
                String mime = cr.getType(uri);
                if (mime.contains("image")) {
                    mImageAttachment = getRealPathFromURI(uri);
//                    makeToast("Photo attachment added");
                    makeToast("mime type = " + mime);
                } else {
                    //ADD FRAGMENT TO DISPLAY LIST
                    mFileAttachment.add(data.getDataString());
                }
            } else if (requestCode == TAKE_IMAGE_REQ_CODE) {
                    mImageAttachment = tempImagePath;
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}
