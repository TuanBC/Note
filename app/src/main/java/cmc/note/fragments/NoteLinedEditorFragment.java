package cmc.note.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import cmc.note.R;
import cmc.note.activities.MainActivity;
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
        return mRootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getCurrentNote();
    }

    public static NoteLinedEditorFragment newInstance(long id){
        NoteLinedEditorFragment fragment = new NoteLinedEditorFragment();

        if (id > 0){
            Bundle bundle = new Bundle();
            bundle.putLong("id", id);
            fragment.setArguments(bundle);
            Log.i("NoteLinedEditorFragment", "done pushing id");
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
        if (TextUtils.isEmpty(content)){
            mContentEditText.setError("Content is required");
            return false;
        }

        String title = mTitleEditText.getText().toString();         //DEFAULT TITLE = CONTENT
        if (TextUtils.isEmpty(title)){
            title=content;
        }

        if (mCurrentNote != null){
            mCurrentNote.setContent(content);
            mCurrentNote.setTitle(title);
            NoteManager.newInstance(getActivity()).update(mCurrentNote);

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }else {
            Note note = new Note();
            note.setTitle(title);
            note.setType("note");
            note.setContent(content);
            NoteManager.newInstance(getActivity()).create(note);

            Intent intent = new Intent(getActivity(), MainActivity.class);
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
        return new SimpleDateFormat("MMM dd, yyyy - h:mm a").format(new Date(date));
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
                startActivity(new Intent(getActivity(), MainActivity.class));
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

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}
