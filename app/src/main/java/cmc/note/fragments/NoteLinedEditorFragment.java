package cmc.note.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cmc.note.R;
import cmc.note.activities.MainActivity;
import cmc.note.activities.NoteEditorActivity;
import cmc.note.data.NoteManager;
import cmc.note.models.Note;

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

    private Calendar date = null;

    private final static String TAG = "NOTEEDITOR Fragment";

    public NoteLinedEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note_lined_editor, container, false);
        mTitleEditText = (EditText)mRootView.findViewById(R.id.edit_text_title);
        mContentEditText = (EditText)mRootView.findViewById(R.id.edit_text_note);
        mTimeAgo = (TextView)mRootView.findViewById(R.id.time_ago);
        mModifiedTime = (TextView)mRootView.findViewById(R.id.modified_time);

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

        return mRootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getCurrentNote();
    }

    public void setListOrder(String s){
        this.mListOrder=s;
    }

    public static NoteLinedEditorFragment newInstance(long id){
        NoteLinedEditorFragment fragment = new NoteLinedEditorFragment();

        if (id > 0){
            Bundle bundle = new Bundle();
            bundle.putLong("id", id);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    private void getCurrentNote() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            Long id = args.getLong("id",0);
            Log.i("log getcurrentnote", "id" + id);
            if (id > 0) {
                mCurrentNote = NoteManager.newInstance(getActivity()).getNote(id);
            }
        }
    }

    private boolean saveNote(){
        String content = mContentEditText.getText().toString();
        String title = mTitleEditText.getText().toString();         //DEFAULT TITLE = CONTENT
        if (TextUtils.isEmpty(title)){
            title="Untitled Note";
        }

        if (date!=null){
            MainActivity a = (MainActivity) getActivity();
            a.scheduleNotification(a.getNotification(title), date.getTimeInMillis()-System.currentTimeMillis());
        }

        if (mCurrentNote != null){
            mCurrentNote.setContent(content);
            mCurrentNote.setTitle(title);
            NoteManager.newInstance(getActivity()).update(mCurrentNote);

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("list_order", mListOrder);
            startActivity(intent);
        }else {
            Note note = new Note();
            note.setTitle(title);
            note.setType("note");
            note.setContent(content);
            NoteManager.newInstance(getActivity()).create(note);

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("list_order", mListOrder);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentNote != null){
            populateFields();
        } else mModifiedTime.setText(getReadableDate(System.currentTimeMillis()));
    }

    //POPULATE INFO OF CURRENT NOTE TO EDIT NOTE
    private void populateFields() {
        mTitleEditText.setText(mCurrentNote.getTitle());
        mContentEditText.setText(mCurrentNote.getContent());

        //TIME
        long temp_modified_time=mCurrentNote.getDateModified();
        mModifiedTime.setText(getReadableDate(temp_modified_time));

        long temp_time_ago = (System.currentTimeMillis() - temp_modified_time)/1000 ;
        if (temp_time_ago < 60) mTimeAgo.setText(temp_time_ago + " seconds ago");
        else if (temp_time_ago < 3600) mTimeAgo.setText(temp_time_ago/60 + " minute(s) ago");
        else if (temp_time_ago < 86400) mTimeAgo.setText(temp_time_ago/3600 + " hour(s) ago");
        else mTimeAgo.setText(temp_time_ago/86400 + " day(s) ago");
    }

    private static String getReadableDate(long date){
        return new SimpleDateFormat("MMM dd, yyyy").format(new Date(date));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_note_edit, menu);
    }

    //MENU ONCLICK LISTENER
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                if (saveNote()) { //IF NULL => BLANK NOTE
                    makeToast(mCurrentNote !=  null ? "Note updated" : "Note saved");
                }
                break;

            case R.id.action_delete:
                if (mCurrentNote != null){
                    promptForDelete();
                }else {
                    getActivity().finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void promptForDelete(){
        final String titleOfNoteTobeDeleted = mCurrentNote.getTitle();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Delete " + titleOfNoteTobeDeleted + " ?");
        alertDialog.setMessage("Are you sure you want to delete the note " + titleOfNoteTobeDeleted + "?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    public void onBackClicked(){
        promptForDiscard();
    }

    public void promptForDiscard(){
        if (mRootView.hasFocus())
            getActivity().finish();
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

    private View.OnClickListener button_listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_reminder:

                    //ADD SOME NOTICE TO USER THAT REMINDER SET

                    if (date!=null) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Reminder");
                        alertDialog.setMessage("What do you want to do?");
                        alertDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                createDateTimePicker();
                            }
                        });
                        alertDialog.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                date=null;
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
                        //OPEN A DIALOG WITH CATEGORY LIST
                    break;
            }
        }
    };

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void createDateTimePicker(){
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Log.v(TAG, "The choosen one " + date.getTime());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }
}
