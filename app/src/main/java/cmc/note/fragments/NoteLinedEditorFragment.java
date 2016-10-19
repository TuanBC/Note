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
        String title = mTitleEditText.getText().toString();

        if (TextUtils.isEmpty(title)){
            mTitleEditText.setError("Title is required");
            return false;
        }

        String content = mContentEditText.getText().toString();
        if (TextUtils.isEmpty(content)){
            mContentEditText.setError("Content is required");
            return false;
        }

        if (mCurrentNote != null){

            Log.i("log NEWNOTE", "NOT NULL");

            mCurrentNote.setContent(content);
            mCurrentNote.setTitle(title);
            NoteManager.newInstance(getActivity()).update(mCurrentNote);

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }else {

            Log.i("log NEWNOTE", "NULL");

            Note note = new Note();
            note.setTitle(title);
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
        }
    }

    //SET TITLE AND CONTENT OF CURRENT NOTE TO EDIT NOTE
    private void populateFields() {
        mTitleEditText.setText(mCurrentNote.getTitle());
        mContentEditText.setText(mCurrentNote.getContent());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_note_edit_lined, menu);
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
                    makeToast("Cannot delete note that has not been saved");
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

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }


}
